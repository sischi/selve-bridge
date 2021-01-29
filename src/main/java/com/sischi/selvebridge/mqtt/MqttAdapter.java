package com.sischi.selvebridge.mqtt;

import javax.annotation.PostConstruct;

import com.sischi.selvebridge.core.entities.properties.MqttProperties;
import com.sischi.selvebridge.core.util.HasLogger;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
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
    private String connectionString = null;



    protected int count = 0;

    @PostConstruct
    protected void init() {
        initConnectionString();
        try {
            mqttClient = new MqttClient(connectionString, "selvebridge");
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            mqttClient.connect(options);
        } catch(Exception ex) {
            getLogger().error("could not connect to mqtt broker '{}'! something went wrong.", connectionString, ex);
        }

        if(isConnected()) {
            getLogger().debug("mqtt client connected succesfully!");
        }
        else {
            getLogger().error("mqtt client connection FAILED!!");
        }
    }

    public void publish(String topic, String message) {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(mqttProperties.getQos());
        mqttMessage.setRetained(mqttProperties.getRetain());
        try {
            mqttClient.publish(topic, mqttMessage);
            getLogger().debug("successfully published mqtt message '"+ message +"' on topic '"+ topic +"'!");
        } catch (MqttException e) {
            getLogger().error("could not publish mqtt message '{}' on topic '{}'!", message, topic);
        }
    }

    private void initConnectionString() {
        connectionString = mqttProperties.getProtocol()
                    + "://"
                    + mqttProperties.getBroker()
                    + ":"
                    + mqttProperties.getPort();
    }

    public void subscribe(String topic, IMqttMessageListener listener) {
        try {
            mqttClient.subscribe(topic, mqttProperties.getQos(), listener);
            getLogger().info("successfully subscribed to topic '{}' for listener '{}'!", topic, listener.getClass().getSimpleName());
        } catch(Exception ex) {
            getLogger().error("could not subscribe to topic '{}' for listener '{}'!", topic, listener.getClass().getSimpleName(), ex);
        }
    }

    protected boolean isConnected() {
        if(mqttClient != null && mqttClient.isConnected()) {
            return true;
        }
        return false;
    }

}
