package com.sischi.selvebridge.rest;

import com.sischi.selvebridge.core.entities.message.SelveXmlMethodCall;
import com.sischi.selvebridge.core.gateway.Conversation;
import com.sischi.selvebridge.util.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/app")
public class SelveRestController extends AbstractSelveRestController {

    @Value("${app.version}")
    private String appVersion;

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return new ResponseEntity<String>("Hi, this is the Selve Bridge version '" + appVersion + "'", HttpStatus.OK);
    }

    @GetMapping("/raw")
    public Conversation sendRaw(@RequestBody SelveXmlMethodCall methodCall) {
        Conversation conversation = selveService.sendSynchronously(methodCall);
        if(conversation.isCanceled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "something went wrong!");
        }
        return conversation;
    }

    @GetMapping("/decodeBase64")
    public ResponseEntity<String> decodeBase64(@RequestParam String base64) {
        return new ResponseEntity<String>("base64: '"+ base64 +"'  --->   binary: '"+ Utils.base64ToBinary(base64) +"'", HttpStatus.OK);
    }

    
}
