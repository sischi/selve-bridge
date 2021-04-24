package com.sischi.selvebridge.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "selvebridge.mqtt")
public class MqttProperties {
    
    private String broker;
    private String protocol = "tcp";
    private Integer port = 1883;
    private Integer qos = 1;
    private Boolean retain = false;
    private String topicPrefix = "selve";
    private String username = null;
    private String password = null;
    private int watchdogInterval = 60;
    private int reconnectInterval = 30;

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getQos() {
        return qos;
    }

    public void setQos(Integer qos) {
        this.qos = qos;
    }

    public Boolean getRetain() {
        return retain;
    }

    public void setRetain(Boolean retain) {
        this.retain = retain;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    public void setTopicPrefix(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }

    public String getPrefix() {
        return topicPrefix;
    }

    public void setPrefix(String prefix) {
        this.topicPrefix = prefix;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
