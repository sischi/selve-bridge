package com.sischi.selvebridge.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "selvebridge.connection")
public class ConnectionProperties {
    
    private String serialPort;
    private int watchdogInterval = 60;
    private int reconnectInterval = 15;

    public String getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(String serialPort) {
        this.serialPort = serialPort;
    }

    public int getWatchdogInterval() {
        return watchdogInterval;
    }

    public void setWatchdogInterval(int watchdogInterval) {
        this.watchdogInterval = watchdogInterval;
    }

    public int getReconnectInterval() {
        return reconnectInterval;
    }

    public void setReconnectInterval(int reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

}
