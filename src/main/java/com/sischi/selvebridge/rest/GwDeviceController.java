package com.sischi.selvebridge.rest;

import com.sischi.selvebridge.core.Conversation;
import com.sischi.selvebridge.core.xml.MessageFactory;
import com.sischi.selvebridge.core.xml.entity.SelveXmlMethodCall;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device")
public class GwDeviceController extends AbstractSelveRestController {
    
    @GetMapping("/scanStart")
    public Conversation scanStart() {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.scanStart());
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/scanStop")
    public Conversation scanStop() {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.scanStop());
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/scanResult")
    public Conversation scanResult() {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.scanResult());
        checkConversation(conversation);
        return conversation;
    }
}
