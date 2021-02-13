package com.sischi.selvebridge.mqtt;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.sischi.selvebridge.core.entities.properties.MqttProperties;
import com.sischi.selvebridge.util.ConnectionWatchdog;
import com.sischi.selvebridge.util.HasLogger;
import com.sischi.selvebridge.util.ReconnectThread;
import com.sischi.selvebridge.util.ConnectionWatchdog.ConnectionWatchdogHandler;
import com.sischi.selvebridge.util.ReconnectThread.ReconnectThreadHandler;

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
public class MqttAdapter implements HasLogger, ConnectionWatchdogHandler, ReconnectThreadHandler {

    @Autowired private MqttProperties mqttProperties;
    private MqttClient mqttClient = null;
    private String connectionString = null;

    private ConnectionWatchdog connectionWatchdog = null;
    private ReconnectThread reconnectThread = null;

    private List<MqttSubscription> subscriptions = new ArrayList<>();

    protected int count = 0;

    @PostConstruct
    protected void init() {
        reconnectThread = new ReconnectThread(
                "mqtt-recon",
                30,
                this
            );
        connectionWatchdog = new ConnectionWatchdog(
                "mqtt-watch",
                60,
                this
            );

        initConnectionString();
        connect();

        if(isConnected()) {
            getLogger().debug("mqtt client succesfully connected to broker '{}'!", connectionString);
            connectionWatchdog.start();
        }
        else {
            getLogger().error("mqtt client connection FAILED!!");
            reconnectThread.start();
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

    public void subscribe(MqttSubscription subscription) {
        subscriptions.add(subscription);
        doSubscribe(subscription);
    }

    protected void restoreSubscriptions() {
        for(MqttSubscription sub : subscriptions) {
            doSubscribe(sub);
        }
    }

    protected void unsubscribeAll() {
        for(MqttSubscription sub : subscriptions) {
            try {
                mqttClient.unsubscribe(sub.getTopic());
            } catch (MqttException e) {
                getLogger().warn("unable to unsubscribe mqtt topic '{}'!", sub.getTopic(), e);
            }
        }
    }

    protected void doSubscribe(MqttSubscription subscription) {
        try {
            mqttClient.subscribe(subscription.getTopic(), mqttProperties.getQos(), subscription.getListener());
            getLogger().info("successfully subscribed to topic '{}' for listener '{}'!", subscription.getTopic(), subscription.getListener().getClass().getSimpleName());
        } catch(Exception ex) {
            getLogger().error("could not subscribe to topic '{}' for listener '{}'!", subscription.getTopic(), subscription.getListener().getClass().getSimpleName(), ex);
        }
    }

    protected boolean isConnected() {
        if(mqttClient != null && mqttClient.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public void connect() {
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
    }

    @Override
    public void disconnect() {
        if(isConnected()) {
            unsubscribeAll();
            try {
                mqttClient.disconnect();
            } catch (MqttException ex) {
                getLogger().warn("could not disconnect from mqtt broker!", ex);
            }
        }
        mqttClient = null;
    }

    @Override
    public void handleSuccessfulReconnect() {
        connectionWatchdog.start();
        restoreSubscriptions();
    }

    @Override
    public boolean checkConnection() {
        return isConnected();
    }

    @Override
    public void handleLostConnection() {
        reconnectThread.start();
    }

    

}
