package com.sischi.selvebridge.mqtt;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sischi.selvebridge.core.SelveBridge;
import com.sischi.selvebridge.core.SelveBridge.SelveXmlMessageHandler;
import com.sischi.selvebridge.core.entities.commeo.CommeoCommandPayload;
import com.sischi.selvebridge.core.entities.commeo.CommeoDeviceState;
import com.sischi.selvebridge.core.entities.commeo.CommeoDeviceStateFactory;
import com.sischi.selvebridge.core.entities.enumerations.MethodNames;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodCall;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodResponse;
import com.sischi.selvebridge.core.entities.properties.MqttProperties;
import com.sischi.selvebridge.core.service.CommeoSelveService;
import com.sischi.selvebridge.util.HasLogger;

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

        // parse device id
        try {
            deviceId = Integer.parseInt(chunks[0]);
        } catch (NumberFormatException ex) {
            getLogger().error("could not parse '{}' to device id!", chunks[0]);
            return;
        }

        // parse command payload
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
        getLogger().debug("processing command '{}' for device id '{}'", commandPayload, deviceId);

        // send command to the device
        selveService.sendCommand(deviceId, commandPayload);

        // query the current state of the device to publish it back to the device's mqtt topic
        CommeoDeviceState deviceState = selveService.requestDeviceState(deviceId);
        publishDeviceState(deviceState);
    }

    protected void publishDeviceState(CommeoDeviceState state) {
        try {
            mqttAdapter.publish(generateStateTopic(state.getDeviceId()), om.writeValueAsString(state));
        } catch (Exception ex) {
            getLogger().error("could not publish device state '{}'!", state, ex);
        }
    }


    @Override
    public void onMethodResponse(SelveXmlMethodResponse response) { /* not intereseted in method responses */ }

    @Override
    public void onMethodCall(SelveXmlMethodCall call) {
        if(MethodNames.COMMAND_RESULT.equals(call.getMethodName())) {
            handleCommandResult(call);
        }
        else if(MethodNames.EVENT_DEVICE.equals(call.getMethodName())) {
            handleDeviceStateChanged(call);
        }
    }

    protected void handleCommandResult(SelveXmlMethodCall call) {
        getLogger().info("received command result: '{}'", call.toString());
    }

    protected void handleDeviceStateChanged(SelveXmlMethodCall call) {
        getLogger().info("received device state changed event: '{}'", call.toString());
        CommeoDeviceState state = CommeoDeviceStateFactory.parseFromDeviceStateEvent(call);
        publishDeviceState(state);
    }
    
}
