package com.sischi.selvebridge.mqtt;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sischi.selvebridge.core.SelveBridge;
import com.sischi.selvebridge.core.SelveBridge.SelveXmlMessageHandler;
import com.sischi.selvebridge.core.entities.commeo.CommeoCommandPayload;
import com.sischi.selvebridge.core.entities.commeo.CommeoDeviceState;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodCall;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodResponse;
import com.sischi.selvebridge.core.entities.properties.MqttProperties;
import com.sischi.selvebridge.core.service.CommeoSelveService;
import com.sischi.selvebridge.core.util.HasLogger;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "selvebridge.mqtt.enabled", havingValue = "true", matchIfMissing = false)
@Component
public class CommeoMqttMessageListener implements HasLogger, IMqttMessageListener, SelveXmlMessageHandler {

    private static final String KEYWORD_COMMAND = "cmnd";
    private static final String KEYWORD_STATE = "state";

    private static final String PROTOCOL = "commeo";
    private static final String RESULT_CALLBACK_METHODNAME = "selve.GW.command.result";

    private String TOPIC_COMMAND = null;

    @Autowired
    private MqttProperties mqttProperties;
    @Autowired
    private MqttAdapter mqttAdapter;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SelveBridge selveBridge;
    @Autowired
    private CommeoSelveService selveService;

    @PostConstruct
    public void init() {
        initTopics();
        makeSubscriptions();
    }

    private void initTopics() {
        TOPIC_COMMAND = mqttProperties.getTopicPrefix() + "/" + PROTOCOL + "/+/" + KEYWORD_COMMAND;
    }

    private String generateStateTopic(int deviceId) {
        return mqttProperties.getTopicPrefix() + "/" + PROTOCOL + "/" + deviceId + "/" + KEYWORD_STATE;
    }

    private void makeSubscriptions() {
        mqttAdapter.subscribe(new MqttSubscription(TOPIC_COMMAND, this));
        selveBridge.addSelveXmlMessageHandler(this);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String message = new String(mqttMessage.getPayload());
        getLogger().debug("received message '{}' on topic '{}'", message, topic);

        // remove the irrelevant part of the topic
        topic = topic.replace(mqttProperties.getTopicPrefix() + "/" + PROTOCOL + "/", "");

        // [deviceId, KEYWORD_COMMAND]
        String[] chunks = topic.split("/");
        Integer deviceId = null;

        try {
            deviceId = Integer.parseInt(chunks[0]);
        } catch (NumberFormatException ex) {
            getLogger().error("could not parse '{}' to aktor id!", chunks[0]);
            return;
        }

        CommeoCommandPayload payload = null;
        try {
            payload = om.readValue(message, CommeoCommandPayload.class);
        } catch (Exception ex) {
            getLogger().error("could not parse payload '{}' to command!", message, ex);
            return;
        }
        processCommand(deviceId, payload);
    }

    protected void processCommand(int deviceId, CommeoCommandPayload commandPayload) {
        getLogger().debug("processing command '{}' for aktor id '{}'", commandPayload, deviceId);

        selveService.sendCommand(deviceId, commandPayload);
        CommeoDeviceState deviceState = selveService.requestDeviceState(deviceId);
        try {
            mqttAdapter.publish(generateStateTopic(deviceId), om.writeValueAsString(deviceState));
        } catch (JsonProcessingException ex) {
            getLogger().error("could not publish device state '{}'!", deviceState, ex);
        }
    }


    @Override
    public void onMethodResponse(SelveXmlMethodResponse response) { /* not intereseted in method responses */ }

    @Override
    public void onMethodCall(SelveXmlMethodCall call) {
        if(RESULT_CALLBACK_METHODNAME.equals(call.getMethodName())) {
            getLogger().info("received command result: '{}'", call.toString());
        }
    }
    
}
