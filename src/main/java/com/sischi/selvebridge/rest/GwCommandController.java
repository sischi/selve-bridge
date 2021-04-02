package com.sischi.selvebridge.rest;

import com.sischi.selvebridge.gateway.connection.Conversation;
import com.sischi.selvebridge.gateway.models.MessageFactory;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/command")
public class GwCommandController extends AbstractSelveRestController {
    
    @GetMapping("/device") 
    public Conversation device(
            @RequestParam(required = true) Integer deviceId,
            @RequestParam(required = true) Integer command,
            @RequestParam(required = true) Integer type,
            @RequestParam(required = true) Integer parameter) {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.Command.device(deviceId, command, type, parameter));
        checkConversation(conversation);
        return conversation;
    }

}
