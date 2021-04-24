package com.sischi.selvebridge.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;

public class MqttSubscription {

    private String topic;
    private IMqttMessageListener listener;

    public MqttSubscription(String topic, IMqttMessageListener listener) {
        this.topic = topic;
        this.listener = listener;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public IMqttMessageListener getListener() {
        return listener;
    }

    public void setListener(IMqttMessageListener listener) {
        this.listener = listener;
    }

    
}
