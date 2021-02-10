package com.sischi.selvebridge.core.gateway;

import java.time.LocalDateTime;

import com.sischi.selvebridge.core.entities.message.SelveXmlMethodCall;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodResponse;

public class Conversation {

    private SelveXmlMethodCall methodCall;
    private SelveXmlMethodResponse response;
    private LocalDateTime timeStarted;
    private LocalDateTime timeFinished;
    private LocalDateTime timeCanceled;

    public Conversation() {}

    public SelveXmlMethodCall getMethodCall() {
        return methodCall;
    }

    public void setMethodCall(SelveXmlMethodCall methodCall) {
        this.methodCall = methodCall;
        timeStarted = LocalDateTime.now();
    }

    public Conversation withMethodCall(SelveXmlMethodCall methodCall) {
        setMethodCall(methodCall);
        return this;
    }

    public SelveXmlMethodResponse getResponse() {
        return response;
    }

    public void setResponse(SelveXmlMethodResponse response) {
        this.response = response;
        timeFinished = LocalDateTime.now();
    }

    public boolean hasResponse() {
        return response != null;
    }

    public LocalDateTime getTimeStarted() {
        return timeStarted;
    }

    public LocalDateTime getTimeFinished() {
        return timeFinished;
    }

    public LocalDateTime getTimeCanceled() {
        return timeCanceled;
    }

    public void cancel() {
        timeCanceled = LocalDateTime.now();
    }

    public boolean isCanceled() {
        return timeCanceled != null;
    }
}
