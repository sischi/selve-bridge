package com.sischi.selvebridge.core.gateway;

import java.nio.charset.Charset;

import javax.annotation.PostConstruct;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import com.sischi.selvebridge.core.entities.properties.ConnectionProperties;
import com.sischi.selvebridge.util.ConnectionWatchdog;
import com.sischi.selvebridge.util.HasLogger;
import com.sischi.selvebridge.util.ReconnectThread;
import com.sischi.selvebridge.util.ConnectionWatchdog.ConnectionWatchdogHandler;
import com.sischi.selvebridge.util.ReconnectThread.ReconnectThreadHandler;

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
public class ConnectionManager implements HasLogger, SerialPortDataListener, ConnectionWatchdogHandler, ReconnectThreadHandler {

    @Autowired
    private ConnectionProperties connectionProperties;

    private SerialPort serialPort = null;

    protected DataReceivedHandler dataReceivedHandler;

    private ReconnectThread reconnectThread = null;
    
    private ConnectionWatchdog connectionWatchdog = null;

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
        reconnectThread = new ReconnectThread(
                "serial-recon",
                connectionProperties.getReconnectInterval(),
                this
            );
        connectionWatchdog = new ConnectionWatchdog(
                "serial-watch",
                connectionProperties.getWatchdogInterval(),
                this
            );
        connect();
        if (isConnected()) {
            connectionWatchdog.start();
        }
        else {
            reconnectThread.start();
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
    @Override
    public void connect() {
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
    @Override
    public void disconnect() {
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

    @Override
    public boolean checkConnection() {
        return isConnected();
    }

    @Override
    public void handleLostConnection() {
        reconnectThread.start();
    }

    @Override
    public void handleSuccessfulReconnect() {
        connectionWatchdog.start();
    }

}
