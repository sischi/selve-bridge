package com.sischi.selvebridge.rest;

import com.sischi.selvebridge.core.Conversation;
import com.sischi.selvebridge.core.xml.MessageFactory;
import com.sischi.selvebridge.core.xml.entity.SelveXmlMethodCall;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/service")
public class GwServiceController extends AbstractSelveRestController {
    
    @GetMapping("/ping")
    public Conversation ping() {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.ping());
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/getState")
    public Conversation getState() {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.getState());
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/getVersion")
    public Conversation getVersion() {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.getVersion());
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/getLed")
    public Conversation getLed() {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.getLed());
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/setLed")
    public Conversation setLed(@RequestParam Integer ledMode) {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.setLed(ledMode));
        checkConversation(conversation);
        return conversation;
    }
}
