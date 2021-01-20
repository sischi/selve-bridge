package com.sischi.selvebridge.mqtt;

import javax.annotation.PostConstruct;

import com.sischi.selvebridge.core.properties.MqttProperties;
import com.sischi.selvebridge.core.util.HasLogger;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MqttAdapter implements HasLogger {

    @Autowired private MqttProperties mqttProperties;
    private MqttClient mqttClient = null;

    protected int count = 0;

    @PostConstruct
    protected void init() throws MqttSecurityException, MqttException {
        mqttClient = new MqttClient(buildConnectionString(), "selvebridge");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        mqttClient.connect(options);

        if(mqttClient.isConnected()) {
            getLogger().debug("mqtt client connected succesfully!");
        }
        else {
            getLogger().error("mqtt client connection FAILED!!");
        }
    }

    //@Scheduled(fixedDelay = 10000)
    protected void sendMessage() throws MqttPersistenceException, MqttException {
        MqttMessage msg = new MqttMessage(new String("hello from selveBridge! count = '"+ count +"'").getBytes());
        msg.setQos(mqttProperties.getQos().getQos());
        msg.setRetained(mqttProperties.getRetain());
        mqttClient.publish("selve/bridge/hello", msg);
        getLogger().debug("mqtt message published!");
    }

    private String buildConnectionString() {
        return mqttProperties.getProtocol()
                    + "://"
                    + mqttProperties.getBroker()
                    + ":"
                    + mqttProperties.getPort();
    }

}
