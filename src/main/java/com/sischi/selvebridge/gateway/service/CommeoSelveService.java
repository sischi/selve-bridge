package com.sischi.selvebridge.gateway.service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sischi.selvebridge.gateway.connection.Conversation;
import com.sischi.selvebridge.gateway.models.MessageFactory;
import com.sischi.selvebridge.gateway.models.commeo.CommeoCommandPayload;
import com.sischi.selvebridge.gateway.models.commeo.CommeoCommandType;
import com.sischi.selvebridge.gateway.models.commeo.CommeoDeviceState;
import com.sischi.selvebridge.gateway.models.commeo.CommeoDeviceStateFactory;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterBase64;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterInt;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;

import org.springframework.stereotype.Component;


@Component
public class CommeoSelveService extends SelveService {


    public Conversation sendCommand(CommeoCommandPayload payload) {
        
        SelveXmlMessage message = null;
        
        switch (payload.getTargetType()) {
            case DEVICE:
                message = MessageFactory.Command.device(
                    payload.getTarget().get(0),
                    payload.getCommand(),
                    CommeoCommandType.MANUAL,
                    payload.getValue()
                );
                break;
            case GROUP:
                message = MessageFactory.Command.group(
                    payload.getTarget().get(0),
                    payload.getCommand(),
                    CommeoCommandType.MANUAL,
                    payload.getValue()
                );
                break;
            case MANUAL_GROUP:
                message = MessageFactory.Command.groupMan(
                    payload.getTarget(),
                    payload.getCommand(),
                    CommeoCommandType.MANUAL,
                    payload.getValue()
                );
                break;
            default:
                throw new UnsupportedOperationException("target type '"+ payload.getTargetType() +"' not supported!");
        }
        
        getLogger().info("sending command '{}'", message);

        Conversation conversation = sendSynchronously((SelveXmlMethodCall) message);
        checkCommandSuccess(conversation);
        return conversation;
    }

    protected boolean checkCommandSuccess(Conversation conversation) {
        if(!checkConversationSuccess(conversation)) {
            return false;
        }

        SelveMethodParameterInt param = (SelveMethodParameterInt) conversation.getResponse().getParameters().get(0);
        if(param.getValue() == 0) {
            getLogger().error("the command '{}' could not be executed: '{}'", conversation.getMethodCall(), conversation.getResponse());
            return false;
        }

        return true;
    }

    public List<CommeoDeviceState> requestDeviceStates(List<Integer> deviceIds) {
        List<CommeoDeviceState> deviceStates = new ArrayList<>();
        if(deviceIds == null || deviceIds.isEmpty()) {
            return deviceStates;
        }

        for(Integer deviceId : deviceIds) {
            CommeoDeviceState state = requestDeviceState(deviceId);
            if(state != null) {
                deviceStates.add(state);
            }
            else {
                getLogger().warn("could not get device state for device id '{}'", deviceId);
            }
        }

        return deviceStates;
    }

    public CommeoDeviceState requestDeviceState(int deviceId) {
        SelveXmlMessage message = MessageFactory.Device.getValues(deviceId);
        getLogger().info("requesting state for device '{}' with request '{}'", deviceId, message);
        Conversation conversation = sendSynchronously((SelveXmlMethodCall) message);

        return parseDeviceState(conversation);
    }

    public List<CommeoDeviceState> requestGroupState(int groupId) {
        getLogger().debug("requesting devices that belongs to group id '{}'", groupId);
        Conversation conversation = sendSynchronously((SelveXmlMethodCall) MessageFactory.Group.read(groupId));
        if(!checkConversationSuccess(conversation)) {
            getLogger().warn("could not identify devices belonging to group id '{}'", groupId);
            return null;
        }

        SelveMethodParameterBase64 devicesParam = (SelveMethodParameterBase64) conversation.getResponse().getParameters().get(1);
        getLogger().debug("found device ids that belongs to group id '{}': '{}'",
                groupId,
                devicesParam != null
                    ? devicesParam.getIds().stream().map(id -> id.toString()).collect(Collectors.joining(", "))
                    : ""
            );
        return requestDeviceStates(devicesParam.getIds());
    }

    public List<CommeoDeviceState> requestManualGroupState(List<Integer> deviceIds) {
        return requestDeviceStates(deviceIds);
    }

    protected CommeoDeviceState parseDeviceState(Conversation conversation) {
        if(!checkConversationSuccess(conversation)) {
            getLogger().warn("could not parse device state from conversation '{}'", conversation);
            return null;
        }
    
        CommeoDeviceState deviceState = CommeoDeviceStateFactory.parseFromDeviceStateResponse(conversation.getResponse());
        return deviceState;
    }

}
