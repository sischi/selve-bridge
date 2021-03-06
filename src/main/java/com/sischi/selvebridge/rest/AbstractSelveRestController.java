package com.sischi.selvebridge.rest;

import com.sischi.selvebridge.gateway.connection.Conversation;
import com.sischi.selvebridge.gateway.exception.ConversationCancelled;
import com.sischi.selvebridge.gateway.service.SelveService;

import org.springframework.beans.factory.annotation.Autowired;

public class AbstractSelveRestController {
    
    @Autowired
    protected SelveService selveService;


    protected void checkConversation(Conversation conversation) {
        if(conversation == null) {
            throw new RuntimeException("something went wrong! No conversation created.");
        }
        if(conversation.isCanceled()) {
            throw new ConversationCancelled(
                "the conversation was cancelled!"
                + (conversation.getResponse() != null && conversation.getResponse().getError() != null
                    ? "code: '"+ conversation.getResponse().getError().getCode() +"', description: '"+ conversation.getResponse().getError().getDescription() +"'"
                    : "")
                );
        }
    }

}
