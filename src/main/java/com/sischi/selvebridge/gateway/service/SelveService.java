package com.sischi.selvebridge.gateway.service;

import com.sischi.selvebridge.gateway.SelveBridge;
import com.sischi.selvebridge.gateway.connection.Conversation;
import com.sischi.selvebridge.gateway.connection.ConversationManager;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodResponse;
import com.sischi.selvebridge.util.HasLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SelveService implements HasLogger {

    @Autowired
    protected SelveBridge selveBridge;

    @Autowired
    protected ConversationManager conversationManager;

    public void sendMessage(SelveXmlMessage message) {
        selveBridge.sendMessage(message);
    }

    public void sendRawXml(String xml) {
        selveBridge.sendRaw(xml);
    }

    public synchronized Conversation sendSynchronously(SelveXmlMethodCall methodCall) {
        Conversation conversation = conversationManager.startConversation(methodCall);
        if (conversation == null) {
            getLogger().error("could not start conversation! the method call '{}' will not be send!", methodCall);
            return null;
        }

        try {
            selveBridge.sendMessage(methodCall);
        } catch (Exception ex) {
            getLogger().error("an error occured sending method call '{}'", methodCall, ex);
            conversationManager.cancelConversation(conversation);
            return conversation;
        }

        getLogger().debug("method call sent successfully! now waiting for the response ...");

        int timeout = 5000;
        int waitingTime = 0;
        while (!conversation.hasResponse() && waitingTime < timeout) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            waitingTime += 100;
        }

        if(waitingTime >= timeout) {
            getLogger().error("timeout hit while waiting for the response!");
            conversationManager.cancelConversation(conversation);
        }

        return conversation;
    }

    protected boolean checkConversationSuccess(Conversation conversation) {
        if(!conversation.hasResponse()) {
            getLogger().error("got no response from selve gateway for command '{}'!", conversation.getMethodCall());
            return false;
        }

        SelveXmlMethodResponse response = conversation.getResponse();
        if(response.isError()) {
            getLogger().error("received error response '{}' for command '{}'", response, conversation.getMethodCall());
            return false;
        }

        return true;
    }

}
