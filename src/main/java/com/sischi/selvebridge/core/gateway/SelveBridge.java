package com.sischi.selvebridge.core.gateway;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.sischi.selvebridge.core.entities.enumerations.MethodNames;
import com.sischi.selvebridge.core.entities.message.SelveXmlMessage;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodCall;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodResponse;
import com.sischi.selvebridge.core.entities.properties.DeadlockProperties;
import com.sischi.selvebridge.core.exception.DeviceAlreadyLockedException;
import com.sischi.selvebridge.core.exception.DeviceNotConnectedException;
import com.sischi.selvebridge.core.gateway.ConnectionManager.DataReceivedHandler;
import com.sischi.selvebridge.core.xml.MessageParser;
import com.sischi.selvebridge.core.xml.MessageParser.IncomingXmlMessageHandler;
import com.sischi.selvebridge.util.HasLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 
 * The {@link SelveBridge} is responsible for the communication with the selve rf usb gateway and talks
 * with the selve gateway via XML messages on the one hand and provides mechanisms for sending and
 * processing the translated POJOs by the application on the other hand.<br>
 * <br>
 * The translation job from xml to POJO and vice-versa is done by the {@link MessageParser} and the
 * connection to the selve gateway via the serial usb connection is maintained by the {@link ConnectionManager}.<br>
 * <br>
 * On top of that, this class maintains the use of the gateway by controlling the usage of the gateway to be compliant
 * with the selve specification.
 * 
 * @author Simon Schiller
 * 
 */
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
    
	/**
	 * @return a list of all currently registered {@link SelveXmlMessageHandler}s
	 */
    public List<SelveXmlMessageHandler> getSelveXmlMessageHandlers() {
        return selveXmlMessageHandlers;
    }

	/**
	 * register the given {@link SelveXmlMessageHandler} if not already already registered
	 * @param handler the {@link SelveXmlMessageHandler} that should be registered
	 */
    public void addSelveXmlMessageHandler(SelveXmlMessageHandler handler) {
        if(!getSelveXmlMessageHandlers().contains(handler)) {
            getSelveXmlMessageHandlers().add(handler);
        }
        else {
            getLogger().info("selve message handler {} already registered!", handler);
        }
    }

	/**
	 * @return a list of all currently registered {@link DeadlockHandler}s
	 */
	public List<DeadlockHandler> getDeadlockHandlers() {
		return deadlockHandlers;
	}

	/**
	 * register the given {@link DeadlockHandler} if not already already registered
	 * @param handler the {@link DeadlockHandler} that should be registered
	 */
	public void addDeadlockHandler(DeadlockHandler handler) {
		if(!getDeadlockHandlers().contains(handler)) {
			getDeadlockHandlers().add(handler);
		}
		else {
			getLogger().info("deadlock handler {} already registered!", handler);
		}
	}

	/**
	 * does the required initializations after the component is created
	 */
    @PostConstruct
    protected void init() {
		connectionManager.setDataReceivedHandler(this);
		messageParser.addIncomingXmlMessageHandler(this);
		startDeadlockWatchdog();
	}

	/**
	 * tries to lock the bridge to be able to issue a command
	 * @return {@code true} if the bridge was locked successfully, {@code false} otherwise
	 */
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

	/**
	 * unlocks the bridge, if it was locked. If the bridge is already unlocked, this method does nothing.
	 */
	private void unlock() {
		if(isBusy) {
			isBusy = false;
			getLogger().debug("selve device unlocked");
		}
		else {
			getLogger().info("selve device is already unlocked, so nothing to do here!");
		}
	}

	/**
	 * start the deadlock watchdog, that periodically checks for a deadlock
	 */
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


	/**
	 * check if a possible deadlock is present. Deadlock in this case means, if the bridge is locked
	 * and the time since the last lock exceeds a configurable threshold. If a deadlock is identified
	 * all registered deadlock handlers will be invoked.
	 */
	private void deadlockPrevention() {
		if(lastLocked == null) {
			getLogger().debug("device was not locked yet, so no deadlock found!");
			return;
		}

		if(!isBusy) {
			getLogger().debug("device is not locked, so no deadlock found!");
			return;
		}

		long sinceLastLocked = Duration.between(lastLocked, LocalDateTime.now()).getSeconds();
		if(sinceLastLocked > deadlockProperties.getThreshold()) {
			getLogger().warn("possible deadlock detected! the device is locked for {} seconds. forcing 'emergency unlock'!", sinceLastLocked);
			unlock();
			for(DeadlockHandler handler : getDeadlockHandlers()) {
				if(handler == null) {
					getLogger().warn("found deadlock handler to benull! so skipping this handler...");
					continue;
				}
				try {
					handler.handleDeadlock();
				} catch(Exception ex) {
					getLogger().warn("unhandled exception in deadlock handler '{}'", handler.getClass().getSimpleName(), ex);
				}
			}
		}
		else {
			getLogger().debug("device is locked for '{}' seconds, so no deadlock found yet. Deadlock threshold is at '{}' seconds", deadlockProperties.getThreshold());
		}
	}

	/**
	 * takes the given {@link SelveXmlMessage}, converts it to the corresponding xml representation and sends it
	 * to the gateway
	 * @param message the {@link SelveXmlMessage} to be sent
	 */
	public void sendMessage(SelveXmlMessage message) {
		getLogger().debug("trying to send message '{}'", message);
		sendRaw(messageParser.messageToXml(message));
	}

	/**
	 * tries to send the given xml message to the gateway by invoking the {@link ConnectionManager} and respecting
	 * the locked state of the bridge.
	 * @param xml the xml message to be sent
	 */
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
		try {
			if(MethodNames.EVENT_LOG.equals(message.getMethodName())) {
				handleIncomingLogEvent(message);
			}
		} catch(Exception ex) {
			getLogger().warn("unhandled exception in event log handler for message '{}'", message, ex);
		}
		
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

	protected void handleIncomingLogEvent(SelveXmlMethodCall message) {
		String log = "GW: [time='"+ message.getParameters().get(1).getValue() +"', "+
						"code='"+ message.getParameters().get(2).getValue() +"', "+
						"value='"+ message.getParameters().get(3).getValue() +"', "+
						"description='"+ message.getParameters().get(4).getValue() +"']";
		switch ((int) message.getParameters().get(0).getValue()) {
			case 1:
				getLogger().warn(log);
				break;
			case 2:
				getLogger().error(log);
				break;
			default:
				getLogger().info(log);
				break;
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
