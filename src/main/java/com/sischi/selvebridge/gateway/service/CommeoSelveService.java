package com.sischi.selvebridge.gateway.service;


import com.sischi.selvebridge.gateway.connection.Conversation;
import com.sischi.selvebridge.gateway.models.MessageFactory;
import com.sischi.selvebridge.gateway.models.commeo.CommeoCommandPayload;
import com.sischi.selvebridge.gateway.models.commeo.CommeoDeviceState;
import com.sischi.selvebridge.gateway.models.commeo.CommeoDeviceStateFactory;
import com.sischi.selvebridge.gateway.models.enums.CommeoCommandType;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodParameter;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodResponse;

import org.springframework.stereotype.Component;


@Component
public class CommeoSelveService extends SelveService {


    public Conversation sendCommand(int deviceId, CommeoCommandPayload payload) {
        
        SelveXmlMessage message = null;
        
        if(payload.getValue() != null) {
            message = MessageFactory.Command.device(
                    deviceId,
                    payload.getCommand().getValue(),
                    CommeoCommandType.MANUAL.getValue(),
                    payload.getValue()
                );
        }
        else {
            message = MessageFactory.Command.device(
                    deviceId,
                    payload.getCommand().getValue(),
                    CommeoCommandType.MANUAL.getValue()
                );
        }
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

        SelveXmlMethodParameter param = response.getParameters().get(0);
        if((Integer) param.getValue() == 0) {
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
