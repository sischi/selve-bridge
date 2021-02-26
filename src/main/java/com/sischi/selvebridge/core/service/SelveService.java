package com.sischi.selvebridge.core.service;

import com.sischi.selvebridge.core.entities.message.SelveXmlMessage;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodCall;
import com.sischi.selvebridge.core.gateway.Conversation;
import com.sischi.selvebridge.core.gateway.ConversationManager;
import com.sischi.selvebridge.core.gateway.SelveBridge;
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

}
