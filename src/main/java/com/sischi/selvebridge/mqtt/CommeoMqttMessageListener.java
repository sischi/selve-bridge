package com.sischi.selvebridge.mqtt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sischi.selvebridge.configuration.properties.MqttProperties;
import com.sischi.selvebridge.gateway.SelveBridge;
import com.sischi.selvebridge.gateway.SelveXmlMessageHandler;
import com.sischi.selvebridge.gateway.models.commeo.CommeoCommandPayload;
import com.sischi.selvebridge.gateway.models.commeo.CommeoCommandTargetType;
import com.sischi.selvebridge.gateway.models.commeo.CommeoDeviceState;
import com.sischi.selvebridge.gateway.models.commeo.CommeoDeviceStateFactory;
import com.sischi.selvebridge.gateway.models.enums.MethodNames;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodResponse;
import com.sischi.selvebridge.gateway.service.CommeoSelveService;
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
    private static final String TARGET_DEVICE = "device";
    private static final String TARGET_GROUP = "group";
    private static final String TARGET_GROUP_MANUAL = "mangroup";

    private String TOPIC_DEVICE = null;
    private String TOPIC_GROUP = null;
    private String TOPIC_GROUP_MANUAL = null;

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
        TOPIC_DEVICE = mqttProperties.getTopicPrefix() + "/" + PROTOCOL + "/" + TARGET_DEVICE + "/+/" + KEYWORD_COMMAND;
        TOPIC_GROUP = mqttProperties.getTopicPrefix() + "/" + PROTOCOL + "/" + TARGET_GROUP + "/+/" + KEYWORD_COMMAND;
        TOPIC_GROUP_MANUAL = mqttProperties.getTopicPrefix() + "/" + PROTOCOL + "/" + TARGET_GROUP_MANUAL + "/+/" + KEYWORD_COMMAND;
    }

    private String generateStateTopic(int deviceId) {
        return mqttProperties.getTopicPrefix() + "/" + PROTOCOL + "/" + TARGET_DEVICE + "/" + deviceId + "/" + KEYWORD_STATE;
    }

    private void makeSubscriptions() {
        mqttAdapter.subscribe(new MqttSubscription(TOPIC_DEVICE, this));
        mqttAdapter.subscribe(new MqttSubscription(TOPIC_GROUP, this));
        mqttAdapter.subscribe(new MqttSubscription(TOPIC_GROUP_MANUAL, this));
        selveBridge.addSelveXmlMessageHandler(this);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String message = new String(mqttMessage.getPayload());
        getLogger().info("received message '{}' on topic '{}'", message, topic);
        
        try {
            handleMessageArrived(topic, message);
        } catch(Exception ex) {
            getLogger().error("an unhandled exception occured while handling incoming message '{}' on topic '{}'!", message, topic, ex);
        }
    }

    private void handleMessageArrived(String topic, String message) {
        // remove the irrelevant part of the topic
        topic = topic.replace(mqttProperties.getTopicPrefix() + "/" + PROTOCOL + "/", "");

        // [targetType, (id), KEYWORD_COMMAND]
        String[] chunks = topic.split("/");

        // parse command payload from message
        CommeoCommandPayload payload = null;
        try {
            payload = om.readValue(message, CommeoCommandPayload.class);
        } catch (Exception ex) {
            getLogger().error("could not parse payload '{}' to command!", message, ex);
            return;
        }

        // parse target type from topic
        CommeoCommandTargetType targetType = parseTargetTypeFromTopic(chunks[0]);
        if(targetType != payload.getTargetType()) {
            getLogger().warn("found target type '{}' in mqtt message payload but was received on '{}' topic! overwriting type of payload because the topic has higher priority.");
            payload.setTargetType(targetType);
        }

        // if the target is part of the topic, we have to parse it, otherwise the target is expected to be a list of device ids
        // that may be empty and be present in the mqtt message payload
        if(targetType == CommeoCommandTargetType.DEVICE || targetType == CommeoCommandTargetType.GROUP) {
            // parse target
            Integer target = null;
            try {
                target = Integer.parseInt(chunks[1]);
            } catch (NumberFormatException ex) {
                getLogger().error("could not parse '{}' to target id!", chunks[1]);
                return;
            }
            payload.setTarget(target);
        }
        
        processCommand(payload);
    }

    private CommeoCommandTargetType parseTargetTypeFromTopic(String topic) {
        if(topic == null) throw new IllegalArgumentException("invalid target type 'null'!");

        if(topic.equals(TARGET_DEVICE)) {
            return CommeoCommandTargetType.DEVICE;
        }
        else if(topic.equals(TARGET_GROUP)) {
            return CommeoCommandTargetType.GROUP;
        }
        else if(topic.equals(TARGET_GROUP_MANUAL)) {
            return CommeoCommandTargetType.MANUAL_GROUP;
        }
        
        throw new IllegalArgumentException("could not parse commeo target type from topic '"+ topic +"'!");
    }

    protected void processCommand(CommeoCommandPayload commandPayload) {
        getLogger().debug("processing command '{}'", commandPayload);

        // send command to the device
        try {
            selveService.sendCommand(commandPayload);
        } catch(Exception ex) {
            getLogger().error("could not send command {}: {}", commandPayload, ex.getMessage(), ex);
        }

        // query the current state of the affected device to publish it back to the device's mqtt topic
        try {
            List<CommeoDeviceState> deviceStates = new ArrayList<>();
            switch (commandPayload.getTargetType()) {
                case DEVICE:
                    deviceStates.add(selveService.requestDeviceState(commandPayload.getTarget().get(0)));
                    break;
                case GROUP:
                    deviceStates = selveService.requestGroupState(commandPayload.getTarget().get(0));
                    break;
                case MANUAL_GROUP:
                    deviceStates = selveService.requestManualGroupState(commandPayload.getTarget());
                    break;
                default:
                    getLogger().warn("unsupported target type '{}'", commandPayload.getTargetType());
                    break;
            }
            if(deviceStates != null) {
                deviceStates.stream()
                        .filter(state -> state != null)
                        .forEach(state -> {
                            publishDeviceState(state);
                        });
            }
        } catch(Exception ex) {
            getLogger().error("could not request state for type '{}' and value '{}'", commandPayload.getTargetType(), commandPayload.getTarget(), ex);
        }
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
        if(MethodNames.Command.RESULT.equals(call.getMethodName())) {
            handleCommandResult(call);
        }
        else if(MethodNames.Event.DEVICE.equals(call.getMethodName())) {
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
