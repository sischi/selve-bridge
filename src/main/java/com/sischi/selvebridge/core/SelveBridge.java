package com.sischi.selvebridge.core;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.sischi.selvebridge.core.entities.message.SelveXmlMessage;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodCall;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodResponse;
import com.sischi.selvebridge.core.entities.properties.DeadlockProperties;
import com.sischi.selvebridge.core.exception.DeviceAlreadyLockedException;
import com.sischi.selvebridge.core.exception.DeviceNotConnectedException;
import com.sischi.selvebridge.core.gateway.ConnectionManager;
import com.sischi.selvebridge.core.gateway.ConnectionManager.DataReceivedHandler;
import com.sischi.selvebridge.core.xml.MessageParser;
import com.sischi.selvebridge.core.xml.MessageParser.IncomingXmlMessageHandler;
import com.sischi.selvebridge.util.HasLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SelveBridge implements HasLogger, DataReceivedHandler, IncomingXmlMessageHandler {

    @Autowired protected ConnectionManager connectionManager;
    @Autowired protected MessageParser messageParser;
	@Autowired private DeadlockProperties deadlockProperties;
	
	private boolean isBusy = false;
	private LocalDateTime lastLocked;
	private Thread deadlockWatchdog = null;

	private List<SelveXmlMessageHandler> selveXmlMessageHandlers = new ArrayList<>();
	private List<DeadlockHandler> deadlockHandlers = new ArrayList<>();

	public interface SelveXmlMessageHandler {
		void onMethodResponse(SelveXmlMethodResponse response);
		void onMethodCall(SelveXmlMethodCall call);
	}

	public interface DeadlockHandler {
		void handleDeadlock();
	}
    
    public List<SelveXmlMessageHandler> getSelveXmlMessageHandlers() {
        return selveXmlMessageHandlers;
    }

    public void addSelveXmlMessageHandler(SelveXmlMessageHandler handler) {
        if(!getSelveXmlMessageHandlers().contains(handler)) {
            getSelveXmlMessageHandlers().add(handler);
        }
        else {
            getLogger().info("selve message handler {} already registered!", handler);
        }
    }

	public List<DeadlockHandler> getDeadlockHandlers() {
		return deadlockHandlers;
	}

	public void addDeadlockHandler(DeadlockHandler handler) {
		if(!getDeadlockHandlers().contains(handler)) {
			getDeadlockHandlers().add(handler);
		}
		else {
			getLogger().info("deadlock handler {} already registered!", handler);
		}
	}

    @PostConstruct
    protected void init() {
		connectionManager.setDataReceivedHandler(this);
		messageParser.addIncomingXmlMessageHandler(this);
		startDeadlockWatchdog();
	}

	private synchronized boolean lock() {
		if(isBusy) {
			getLogger().warn("could not lock selve device due to it is busy");
			return false;
		}

		isBusy = true;
		lastLocked = LocalDateTime.now();
		getLogger().debug("selve device locked successfully!");
		return true;
	}

	private void unlock() {
		if(isBusy) {
			isBusy = false;
			getLogger().debug("selve device unlocked");
		}
		else {
			getLogger().info("selve device is already unlocked, so nothing to do here!");
		}
	}


	private void startDeadlockWatchdog() {
        if (deadlockProperties.getWatchdogInterval() <= 0) {
            getLogger().warn("deadlock watchdog is disabled due to corresponding deadlock property is set to '"+ deadlockProperties.getWatchdogInterval() +"'!");
            return;
        }

        if (deadlockWatchdog == null || !deadlockWatchdog.isAlive()) {
            deadlockWatchdog = new Thread(() -> {
                getLogger().info("deadlock watchdog started ...");
                while (true) {
					deadlockPrevention();
                    try {
                        Thread.sleep(deadlockProperties.getWatchdogInterval() * 1000);
                    } catch (InterruptedException e) {
						getLogger().warn("deadlock watchdog got interrupted!", e);
                    }
                }
            });
            deadlockWatchdog.setName("dl-watch");
            deadlockWatchdog.start();
        }
    }



	private void deadlockPrevention() {
		if(lastLocked == null) {
			getLogger().trace("device was not locked yet, so no deadlock found!");
			return;
		}

		if(!isBusy) {
			getLogger().trace("device is not locked, so no deadlock found!");
			return;
		}

		long sinceLastLocked = Duration.between(lastLocked, LocalDateTime.now()).toSeconds();
		if(sinceLastLocked > deadlockProperties.getThreshold()) {
			getLogger().warn("possible deadlock detected! the device is locked for {} seconds. forcing 'emergency unlock'!", sinceLastLocked);
			unlock();
			for(DeadlockHandler handler : getDeadlockHandlers()) {
				handler.handleDeadlock();
			}
		}
		else {
			getLogger().trace("device is locked for {} seconds, so no deadlock found yet. Deadlock threshold is at {} seconds", deadlockProperties.getThreshold());
		}
	}

	public void sendMessage(SelveXmlMessage message) {
		getLogger().debug("trying to send message '{}'", message);
		sendRaw(messageParser.messageToXml(message));
	}

	public void sendRaw(String xml) {
		if(connectionManager.isConnected()) {
			getLogger().info("port is active!");
			if(lock()) {
				getLogger().debug("successfully locked device for writing!");
				getLogger().info("sending xml: {} ...", xml);
				connectionManager.write(xml);
				getLogger().info("xml sent successful!");
			}
			else {
				getLogger().error("cannot lock device, due to there is already a method call in progress!");
				throw new DeviceAlreadyLockedException("device busy!");
			}
		}
		else {
			getLogger().error("connection found to be not active! skip sending xml '{}'!", xml);
			throw new DeviceNotConnectedException("device is not connected!");
		}
	}

	
	public void handleMethodCall(SelveXmlMethodCall message) {
		getLogger().debug("processing incoming method call by invoking '{}' registered handlers", getSelveXmlMessageHandlers().size());
		for(SelveXmlMessageHandler handler : getSelveXmlMessageHandlers()) {
			try {
				handler.onMethodCall(message);
			} catch(Exception ex) {
				getLogger().warn("unhandled exception in method call handler for message '{}'", message, ex);
			}
		}
	}

	public void handleMethodResponse(SelveXmlMethodResponse message) {
		unlock();
		getLogger().debug("processing incoming method response by invoking '{}' registered handlers", getSelveXmlMessageHandlers().size());
		for(SelveXmlMessageHandler handler : getSelveXmlMessageHandlers()) {
			try {
				handler.onMethodResponse(message);
			} catch(Exception ex) {
				getLogger().warn("unhandled exception in method response handler for message '{}'", message, ex);
			}
		}
	}

	public void handleIncomingMessage(SelveXmlMessage message) {
		getLogger().debug("processing incoming messsage '{}'", message);
        switch (message.getType()) {
			case METHOD_CALL:
				handleMethodCall((SelveXmlMethodCall) message);
                break;
            case METHOD_RESPONSE:
                handleMethodResponse((SelveXmlMethodResponse) message);
                break;
            default:
                getLogger().error("got unexpected message type for message '{}'! skipping this one.", message);
                break;
        }
    }

	@Override
	public void onIncomingXmlMessage(SelveXmlMessage message) {
		handleIncomingMessage(message);
	}

	@Override
	public void onDataReceived(String data) {
		messageParser.processRawData(data);
	}

}
