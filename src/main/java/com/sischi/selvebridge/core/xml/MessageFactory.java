package com.sischi.selvebridge.core.xml;

import com.sischi.selvebridge.core.xml.entity.SelveXmlMessage;
import com.sischi.selvebridge.core.xml.entity.SelveXmlMethodCall;
import com.sischi.selvebridge.core.xml.entity.SelveXmlMethodParameter;
import com.sischi.selvebridge.core.xml.entity.SelveXmlMethodParameter.ParameterType;

public class MessageFactory {

    public static SelveXmlMessage ping() {
        return new SelveXmlMethodCall()
            .withMethodName("selve.GW.service.ping");
    }

    public static SelveXmlMessage getState() {
        return new SelveXmlMethodCall()
            .withMethodName("selve.GW.service.getState");
    }

    public static SelveXmlMessage getVersion() {
        return new SelveXmlMethodCall()
            .withMethodName("selve.GW.service.getVersion");
    }

    public static SelveXmlMessage getLed() {
        return new SelveXmlMethodCall()
            .withMethodName("selve.GW.service.getLED");
    }

    public static SelveXmlMessage setLed(int ledMode) {
        return new SelveXmlMethodCall()
            .withMethodName("selve.GW.service.setLED")
            .withParameter(
                new SelveXmlMethodParameter(ParameterType.INT, ledMode)
            );
    }

    public static SelveXmlMessage scanStart() {
        return new SelveXmlMethodCall()
            .withMethodName("selve.GW.device.scanStart");
    }

    public static SelveXmlMessage scanStop() {
        return new SelveXmlMethodCall()
            .withMethodName("selve.GW.device.scanStop");
    }

    public static SelveXmlMessage scanResult() {
        return new SelveXmlMethodCall()
            .withMethodName("selve.GW.device.scanResult");
    }
}
