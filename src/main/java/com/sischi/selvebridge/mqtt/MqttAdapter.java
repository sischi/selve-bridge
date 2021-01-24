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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@ConditionalOnProperty(
    name = "selvebridge.mqtt.enabled",
    havingValue = "true",
    matchIfMissing = false
)
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

    public void publish(String topic, String message) {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(mqttProperties.getQos().getQos());
        mqttMessage.setRetained(mqttProperties.getRetain());
        try {
            mqttClient.publish(topic, mqttMessage);
            getLogger().debug("successfully published mqtt message '"+ message +"' on topic '"+ topic +"'!");
        } catch (MqttException e) {
            getLogger().error("could not publish mqtt message '{}' on topic '{}'!", message, topic);
        }
    }

    private String buildConnectionString() {
        return mqttProperties.getProtocol()
                    + "://"
                    + mqttProperties.getBroker()
                    + ":"
                    + mqttProperties.getPort();
    }

}
