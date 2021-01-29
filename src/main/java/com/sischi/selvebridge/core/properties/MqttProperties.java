package com.sischi.selvebridge.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "selvebridge.mqtt")
public class MqttProperties {
    
    private String broker;
    private String protocol = "tcp";
    private Integer port = 1883;
    private MqttQos qos = MqttQos.EXACTLY_ONCE;
    private Boolean retain = true;
    private String topicPrefix = "selve";

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

    public MqttQos getQos() {
        return qos;
    }

    public void setQos(MqttQos qos) {
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
}
