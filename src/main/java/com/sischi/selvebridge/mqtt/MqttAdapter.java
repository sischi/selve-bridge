package com.sischi.selvebridge.mqtt;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.sischi.selvebridge.configuration.properties.MqttProperties;
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


/**
 * 
 * This class connects to the mqtt broker specified by the {@link MqttProperties}. To be able to react on a
 * connection loss, a {@link ConnectionWatchdog} is started in its own thread after the connection is 
 * established successfully. If a connection loss is detected, a {@link ReconnectThread} periodically
 * tries to reconnect until a connection could be established.
 * <br><br>
 * 
 * It is responsible to perform subscriptions and to recover them after a successful reconnect and to publish
 * messages on the mqtt broker.
 * 
 * 
 * @author Simon Schiller
 * 
 */
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

    /**
     * make some initializations
     */
    @PostConstruct
    protected void init() {
        // initialize reconnect thread
        reconnectThread = new ReconnectThread(
                "mqtt-recon",
                30,
                this
            );
        // initialize connection watchdog
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

    /**
     * publish a message on the given topic
     * @param topic the topic on that the message should be published
     * @param message the message that should be published
     */
    public void publish(String topic, String message) {
        // create the mqtt message with the information provided and configured via properties
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(mqttProperties.getQos());
        mqttMessage.setRetained(mqttProperties.getRetain());

        // try to publish the mqtt message
        try {
            mqttClient.publish(topic, mqttMessage);
            getLogger().debug("successfully published mqtt message '"+ message +"' on topic '"+ topic +"'!");
        } catch (MqttException e) {
            getLogger().error("could not publish mqtt message '{}' on topic '{}'!", message, topic);
        }
    }

    /**
     * build the connection string used to connect to the mqtt broker
     */
    private void initConnectionString() {
        connectionString = mqttProperties.getProtocol()
                    + "://"
                    + mqttProperties.getBroker()
                    + ":"
                    + mqttProperties.getPort();
    }

    /**
     * add a mqtt subscription
     * @param subscription the {@link MqttSubscription} that should be made
     */
    public void subscribe(MqttSubscription subscription) {
        subscriptions.add(subscription);
        getLogger().info("successfully registered subscription for topic '{}'", subscription.getTopic());
        if(isConnected()) {
            doSubscribe(subscription);
        }
        else {
            getLogger().warn("mqtt client is not connected, so cannot subscribe to topic '{}' now", subscription.getTopic());
        }
    }

    /**
     * restores all known subscriptions
     */
    protected void restoreSubscriptions() {
        for(MqttSubscription sub : subscriptions) {
            doSubscribe(sub);
        }
    }

    /**
     * unsubscribe all known subscriptions
     */
    protected void unsubscribeAll() {
        for(MqttSubscription sub : subscriptions) {
            try {
                mqttClient.unsubscribe(sub.getTopic());
            } catch (MqttException e) {
                getLogger().warn("unable to unsubscribe mqtt topic '{}'!", sub.getTopic(), e);
            }
        }
    }

    /**
     * actually makes a subscription against the mqtt broker according to the given {@link MqttSubscription}
     */
    protected void doSubscribe(MqttSubscription subscription) {
        try {
            mqttClient.subscribe(subscription.getTopic(), mqttProperties.getQos(), subscription.getListener());
            getLogger().info("successfully subscribed to topic '{}' for listener '{}'!", subscription.getTopic(), subscription.getListener().getClass().getSimpleName());
        } catch(Exception ex) {
            getLogger().error("could not subscribe to topic '{}' for listener '{}'!", subscription.getTopic(), subscription.getListener().getClass().getSimpleName(), ex);
        }
    }

    /**
     * checks whether the connection to the mqtt broker is still valid
     * @return {@code true} if the connection is still valid, {@code false} otherwise
     */
    protected boolean isConnected() {
        if(mqttClient != null && mqttClient.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * tries to connect to the mqtt broker
     */
    @Override
    public void connect() {
        try {
            mqttClient = new MqttClient(connectionString, "selvebridge");
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            // if a username is configured add username and password authentication
            if(mqttProperties.getUsername() != null && mqttProperties.getUsername().length() > 0) {
                options.setUserName(mqttProperties.getUsername());
                options.setPassword(mqttProperties.getPassword().toCharArray());
                getLogger().debug("using auth: '{}'", mqttProperties.getUsername());
            }
            else {
                getLogger().debug("using auth: no!");
            }
            mqttClient.connect(options);
        } catch(Exception ex) {
            getLogger().error("could not connect to mqtt broker '{}'! something went wrong.", connectionString, ex);
        }
    }

    /**
     * disconnects from the mqtt broker
     */
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
