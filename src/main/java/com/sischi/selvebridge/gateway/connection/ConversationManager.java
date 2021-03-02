package com.sischi.selvebridge.gateway.connection;

import javax.annotation.PostConstruct;

import com.sischi.selvebridge.gateway.SelveBridge;
import com.sischi.selvebridge.gateway.SelveXmlMessageHandler;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodResponse;
import com.sischi.selvebridge.util.HasLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ConversationManager implements HasLogger, SelveXmlMessageHandler, DeadlockHandler {

    @Autowired
    private SelveBridge selveBridge;

    private Conversation activeConversation = null;


    @PostConstruct
    protected void init() {
        selveBridge.addSelveXmlMessageHandler(this);
    }

    public synchronized Conversation startConversation(SelveXmlMethodCall methodCall) {
        if(activeConversation == null) {
            getLogger().debug("starting new conversation for method call '{}'", methodCall);
            activeConversation = new Conversation().withMethodCall(methodCall);
            return activeConversation;
        }
        else {
            getLogger().warn("could not start a new conversation for method call '{}' because there is already an active conversation!", methodCall);
            return null;
        }
    }

    public void cancelConversation(Conversation conversation) {
        if(activeConversation != null) {
            getLogger().trace("found an active conversation");
            if(activeConversation == conversation) {
                getLogger().trace("the active conversation is the same as the one that should be cancelled");
                getLogger().info("active conversation cancelled successfully");
                activeConversation.cancel();
                activeConversation = null;
            }
            else {
                getLogger().warn("the active conversation does not match the one to be cancelled! the active conversation will NOT be cancelled!");
            }
        }
        else {
            getLogger().info("no active conversation found, so nothing to do here!");
        }
    }

    @Override
    public void onMethodResponse(SelveXmlMethodResponse response) {
        if(activeConversation != null) {
            getLogger().debug("found active conversation");
            if(!activeConversation.hasResponse()) {
                getLogger().debug("putting the response '{}' in the active conversation", response);
                activeConversation.setResponse(response);
                getLogger().debug("active conversation finished with response '{}'. Forgetting this conversion.", response);
                activeConversation = null;
            }
            else {
                getLogger().warn("currently active conversation already has a response! ignoring the response '{}'", response);
            }
        }
        else {
            getLogger().warn("no active conversation found! ignoring the incoming response '{}'", response);
        }
    }

    @Override
    public void onMethodCall(SelveXmlMethodCall call) {
        getLogger().debug("not interested in incoming method calls! so nothing to do here.");
    }

    @Override
    public void handleDeadlock() {
        if(activeConversation != null) {
            cancelConversation(activeConversation);
        }
    }
    


}
