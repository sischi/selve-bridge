package com.sischi.selvebridge.rest;

import com.sischi.selvebridge.core.Conversation;
import com.sischi.selvebridge.core.xml.entity.SelveXmlMethodCall;
import com.sischi.selvebridge.core.xml.factory.MessageFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device")
public class GwDeviceController extends AbstractSelveRestController {
    
    @GetMapping("/getIds")
    public Conversation getIds() {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.Device.getIds());
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/scanStart")
    public Conversation scanStart() {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.Device.scanStart());
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/scanStop")
    public Conversation scanStop() {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.Device.scanStop());
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/scanResult")
    public Conversation scanResult() {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.Device.scanResult());
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/getInfo")
    public Conversation getInfo(@RequestParam(required = true) Integer aktorId) {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.Device.getInfo(aktorId));
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/save")
    public Conversation save(@RequestParam(required = true) Integer aktorId) {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.Device.save(aktorId));
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/getValues")
    public Conversation getValues(@RequestParam(required = true) Integer aktorId) {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.Device.getValues(aktorId));
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/setFunction")
    public Conversation setFunction(@RequestParam(required = true) Integer aktorId, @RequestParam(required = true) Integer function) {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.Device.setFunction(aktorId, function));
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/setLabel")
    public Conversation setLabel(@RequestParam(required = true) Integer aktorId, @RequestParam(required = true) String label) {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.Device.setLabel(aktorId, label));
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/setType")
    public Conversation setType(@RequestParam(required = true) Integer aktorId, @RequestParam(required = true) Integer type) {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.Device.setType(aktorId, type));
        checkConversation(conversation);
        return conversation;
    }

    @GetMapping("/delete")
    public Conversation delete(@RequestParam Integer aktorId) {
        Conversation conversation = selveService.sendSynchronously((SelveXmlMethodCall) MessageFactory.Device.delete(aktorId));
        checkConversation(conversation);
        return conversation;
    }

}
