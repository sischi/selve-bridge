package com.sischi.selvebridge.gateway.service;


import java.util.ArrayList;
import java.util.List;

import com.sischi.selvebridge.gateway.connection.Conversation;
import com.sischi.selvebridge.gateway.models.MessageFactory;
import com.sischi.selvebridge.gateway.models.commeo.CommeoCommandPayload;
import com.sischi.selvebridge.gateway.models.commeo.CommeoCommandType;
import com.sischi.selvebridge.gateway.models.commeo.CommeoDeviceState;
import com.sischi.selvebridge.gateway.models.commeo.CommeoDeviceStateFactory;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterInt;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodResponse;

import org.springframework.stereotype.Component;


@Component
public class CommeoSelveService extends SelveService {


    public Conversation sendCommand(CommeoCommandPayload payload) {
        
        SelveXmlMessage message = null;
        
        switch (payload.getTargetType()) {
            case DEVICE:
                message = MessageFactory.Command.device(
                    (int) payload.getTarget(),
                    payload.getCommand().getValue(),
                    CommeoCommandType.MANUAL.getValue(),
                    payload.getValue()
                );
                break;
            case GROUP:
                message = MessageFactory.Command.group(
                    (int) payload.getTarget(),
                    payload.getCommand().getValue(),
                    CommeoCommandType.MANUAL.getValue(),
                    payload.getValue()
                );
                break;
            case MANUAL_GROUP:
                // TODO implement message for manual group
                break;
        
            default:
                throw new UnsupportedOperationException("target type '"+ payload.getTargetType() +"' not supported!");
        }
        // TODO check target type and handle different target types
        
        
        getLogger().info("sending command '{}'", message);

        Conversation conversation = sendSynchronously((SelveXmlMethodCall) message);
        checkCommandSuccess(conversation);
        return conversation;
    }

    protected boolean checkCommandSuccess(Conversation conversation) {
        if(!conversation.hasResponse()) {
            getLogger().error("got no response from selve gateway for command '{}'!", conversation.getMethodCall());
            return false;
        }

        SelveXmlMethodResponse response = conversation.getResponse();
        if(response.isError()) {
            getLogger().error("received error response '{}' for command '{}'", response, conversation.getMethodCall());
            return false;
        }

        SelveMethodParameterInt param = (SelveMethodParameterInt) response.getParameters().get(0);
        if(param.getValue() == 0) {
            getLogger().error("the command '{}' could not be executed: '{}'", conversation.getMethodCall(), response);
            return false;
        }

        return true;
    }

    public CommeoDeviceState requestDeviceState(int deviceId) {
        SelveXmlMessage message = MessageFactory.Device.getValues(deviceId);
        getLogger().info("requesting state for device '{}' with request '{}'", deviceId, message);
        Conversation conversation = sendSynchronously((SelveXmlMethodCall) message);

        return parseDeviceState(conversation);
    }

    public List<CommeoDeviceState> requestGroupState(int groupId) {
        List<CommeoDeviceState> deviceStates = new ArrayList<>();
        getLogger().warn("'requestManualGroupState' is not yet implemented!");
        return deviceStates;
    }

    public List<CommeoDeviceState> requestManualGroupState(String mask) {
        List<CommeoDeviceState> deviceStates = new ArrayList<>();
        getLogger().warn("'requestManualGroupState' is not yet implemented!");
        return deviceStates;
    }

    protected CommeoDeviceState parseDeviceState(Conversation conversation) {
        if(!conversation.hasResponse()) {
            getLogger().error("got no response from selve gateway for command '{}'!", conversation.getMethodCall());
            return null;
        }

        SelveXmlMethodResponse response = conversation.getResponse();
        if(response.isError()) {
            getLogger().error("received error response '{}' for command '{}'", response, conversation.getMethodCall());
            return null;
        }
    
        CommeoDeviceState deviceState = CommeoDeviceStateFactory.parseFromDeviceStateResponse(response);
        return deviceState;
    }

}
