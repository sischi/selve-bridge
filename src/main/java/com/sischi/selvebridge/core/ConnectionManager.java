package com.sischi.selvebridge.core;

import java.nio.charset.Charset;
import java.util.concurrent.Semaphore;

import javax.annotation.PostConstruct;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import com.sischi.selvebridge.core.entities.properties.ConnectionProperties;
import com.sischi.selvebridge.core.util.HasLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * This class is responsible to establish and maintain the connection to the
 * serial port that belongs to the selve XML-Gateway. It listens to the serial
 * port, reads any data received and process them by passing them to registered
 * callbacks. Further it provides a mechanism to send data to the selve gateway
 * on the serial port.
 * 
 * @author sischi
 */
@Component
public class ConnectionManager implements HasLogger, SerialPortDataListener {

    @Autowired
    private ConnectionProperties connectionProperties;

    private SerialPort serialPort = null;

    protected DataReceivedHandler dataReceivedHandler;

    private Thread reconnectThread = null;
    private Thread connectionWatchdog = null;

    private Semaphore reconnectMutex = new Semaphore(1);

    /**
     * Interface that defines a callback to handle data received on the serial port
     */
    public interface DataReceivedHandler {

        /**
         * should process data received on the serial port
         * 
         * @param data the data received on the serial port
         */
        void onDataReceived(String data);
    }

    /**
     * this method gets automatically invoked after creation of the spring component
     * and tries to establish a connection to the serial port.
     */
    @PostConstruct
    protected void postConstruct() {
        connect();
        if (isConnected()) {
            startConnectionWatchdog();
        }
        else {
            startReconnectThread();
        }

    }

    protected void startReconnectThread() {
        if (connectionProperties.getReconnectInterval() <= 0) {
            getLogger().warn("reconnect thread is disabled due to crresponding connection property is set to '"+ connectionProperties.getReconnectInterval() +"'!");
            return;
        }

        if (reconnectMutex.tryAcquire()) {
            if (reconnectThread == null || !reconnectThread.isAlive()) {
                reconnectThread = new Thread(() -> {
                    getLogger().debug("reconnect thread started ...");
                    while (!isConnected()) {
                        getLogger().info("serial port is still not connected, so will try again soon ...");
                        disconnect();
                        try {
                            getLogger().debug("waiting for "+ connectionProperties.getReconnectInterval() +" sec ...");
                            Thread.sleep(connectionProperties.getReconnectInterval() * 1000);
                        } catch (InterruptedException e) {
                            getLogger().warn("reconnect thread got interrupted!", e);
                        }
                        connect();
                    }
                    getLogger().debug("serial port connected successfully. finishing reconnect thread and starting connection watchdog!");
                    startConnectionWatchdog();
                });
                reconnectThread.setName("reconnect");
                reconnectThread.start();
            } else {
                getLogger().info("a reconnect thread is already running!");
            }
            reconnectMutex.release();
        } else {
            getLogger().info("could not acquire reconnect mutex");
        }
    }

    private void startConnectionWatchdog() {
        if (connectionProperties.getWatchdogInterval() <= 0) {
            getLogger().warn("connection watchdog is disabled due to corresponding connection property is set to '"+ connectionProperties.getReconnectInterval() +"'!");
            return;
        }

        if (connectionWatchdog == null || !connectionWatchdog.isAlive()) {
            connectionWatchdog = new Thread(() -> {
                getLogger().info("connection watchdog started ...");
                while (isConnected()) {
                    getLogger().debug("the connection to the serial port is still alive, so nothing to do here");
                    try {
                        Thread.sleep(connectionProperties.getWatchdogInterval() * 1000);
                    } catch (InterruptedException e) {
                        getLogger().warn("connection watchdog got interrupted!", e);
                    }
                }
                getLogger().warn("the connection to the serial port lost! starting reconnect thread.");
                startReconnectThread();
            });
            connectionWatchdog.setName("con-watch");
            connectionWatchdog.start();
        }
    }

    /**
     * set up the connection parameter for the serial connection with the information
     * available.
     */
    protected void initalizeSerialPort() {
        disconnect();
        getLogger().info("setting up serial port '{}' ...", connectionProperties.getSerialPort());
        try {
            serialPort = SerialPort.getCommPort(connectionProperties.getSerialPort());
            if (serialPort != null) {
                serialPort.setBaudRate(115200);
                serialPort.setParity(SerialPort.NO_PARITY);
                serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
                serialPort.addDataListener(this);
                getLogger().info("serial port setup successfully!");
            } else {
                getLogger().error("an error occured while setting up the serial port '{}'", connectionProperties.getSerialPort());
            }
        } catch(SerialPortInvalidPortException ex) {
            getLogger().error("an error occured while setting up the serial port '{}'", connectionProperties.getSerialPort(), ex);
        }
    }

    /**
     * set up the connection by configuring the serial port and tries to open the serial connection.
     */
    protected void connect() {
        initalizeSerialPort();
        if (serialPort != null) {
            boolean success = serialPort.openPort();
            if (success) {
                getLogger().info("serial port '{}' successfully connected.", connectionProperties.getSerialPort());
            } else {
                getLogger().error("serial port '{}' could not be connected!", connectionProperties.getSerialPort());
                // TODO: throw a proper exception
            }
        }
    }

    /**
     * disconnect and reset the serial port
     */
    protected void disconnect() {
        if(isConnected()) {
            getLogger().debug("the serial port is currently connected, so disconnecting it.");
            serialPort.closePort();
        }
        else {
            getLogger().debug("the serial port is not connected, so no need to disconnect it");
        }
        getLogger().debug("resetting the serial port configuration");
        serialPort = null;
    }

    /**
     * setter for the data received callback
     * @param handler the data received handler that should be registered
     */
    public void setDataReceivedHandler(DataReceivedHandler handler) {
        this.dataReceivedHandler = handler;
    }

    /**
     * indicates whether the connection to the serial port is established or not
     * @return true if the serial port is connected, false otherwise
     */
    public boolean isConnected() {
        if (serialPort != null && serialPort.isOpen()) {
            return true;
        }
        return false;
    }

    /**
     * method that writes the provided data to the serial port
     * @param data the data that should be written
     */
    public synchronized void write(String data) {
        byte[] rawData = data.getBytes();
        serialPort.writeBytes(rawData, rawData.length);
    }


    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        byte[] rawData = event.getReceivedData();
        getLogger().trace("received '{}' bytes", rawData.length);
        String data = new String(rawData, Charset.forName("UTF-8"));
        getLogger().trace("received data: '{}'", data);
        if(dataReceivedHandler != null) {
            dataReceivedHandler.onDataReceived(data);
        }
        else {
            getLogger().warn("no handler for incoming data registered! so ignoring received data '{}'", data);
        }
    }

}
