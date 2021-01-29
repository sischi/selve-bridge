package com.sischi.selvebridge.mqtt;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sischi.selvebridge.core.entities.control.CommeoCommandPayload;
import com.sischi.selvebridge.core.entities.properties.MqttProperties;
import com.sischi.selvebridge.core.util.HasLogger;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
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
public class CommeoMqttMessageListener implements HasLogger, IMqttMessageListener {

    private static String KEYWORD_COMMAND = "cmnd";
    private static String KEYWORD_STATE = "state";
    private static String KEYWORD_INFO = "info";
    
    private static String PROTOCOL = "commeo";

    private String TOPIC_COMMAND = null;
    
    @Autowired private MqttProperties mqttProperties;
    @Autowired private MqttAdapter mqttAdapter;

    @Autowired private ObjectMapper om;

    @PostConstruct
    public void init() {
        initTopics();
        makeSubscription();
    }


    private void initTopics() {
        TOPIC_COMMAND = mqttProperties.getTopicPrefix() +"/"+ PROTOCOL +"/+/"+ KEYWORD_COMMAND;
    }

    private String generateStateTopic(int aktorId) {
        return mqttProperties.getTopicPrefix() +"/"+ PROTOCOL +"/"+ aktorId +"/"+ KEYWORD_STATE;
    }

    private void makeSubscription() {
        mqttAdapter.subscribe(TOPIC_COMMAND, this);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String message = new String(mqttMessage.getPayload());
        getLogger().debug("received message '{}' on topic '{}'", message, topic);

        // remove the irrelevant part of the topic
        topic = topic.replace(mqttProperties.getTopicPrefix() +"/"+ PROTOCOL +"/", "");

        // [aktorId, KEYWORD_COMMAND]
        String[] chunks = topic.split("/");
        Integer aktorId = null;
        
        try {
            aktorId = Integer.parseInt(chunks[0]);
        } catch(NumberFormatException ex) {
            getLogger().error("could not parse '{}' to aktor id!", chunks[0]);
            return;
        }

        CommeoCommandPayload payload = null;
        try {
            payload = om.readValue(message, CommeoCommandPayload.class);
        } catch(Exception ex) {
            getLogger().error("could not parse payload '{}' to command!", message, ex);
            return;
        }
        processCommand(aktorId, payload);
    }


    protected void processCommand(int aktorId, CommeoCommandPayload commandPayload) {
        getLogger().debug("processing command '{}' for aktor id '{}'", commandPayload, aktorId);

        String message = "{\"position\":"+ commandPayload.getValue() +"}";
        try {
            mqttAdapter.publish(generateStateTopic(aktorId), message);
        } catch(Exception ex) {
            getLogger().error("something went wrong publishing message '{}' on topic '{}'", message, generateStateTopic(aktorId), ex);
        }
    }


    protected void publishError(String message) {

    }
    
}
