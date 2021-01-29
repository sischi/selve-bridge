package com.sischi.selvebridge.core.entities.factories;

import com.sischi.selvebridge.core.entities.message.SelveXmlMessage;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodCall;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodParameter;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodParameter.ParameterType;
import com.sischi.selvebridge.core.util.Validator;

public class MessageFactory {

    public static class Service {
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
    }


    public static class Device {
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
        public static SelveXmlMessage getInfo(int aktorId) {
            Validator.validateCommeoAktorId(aktorId);
            return new SelveXmlMethodCall()
                .withMethodName("selve.GW.device.getInfo")
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, aktorId)
                );
        }
        public static SelveXmlMessage save(int aktorId) {
            Validator.validateCommeoAktorId(aktorId);
            return new SelveXmlMethodCall()
                .withMethodName("selve.GW.device.save")
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, aktorId)
                );
        }
        public static SelveXmlMessage getValues(int aktorId) {
            Validator.validateCommeoAktorId(aktorId);
            return new SelveXmlMethodCall()
                .withMethodName("selve.GW.device.getValues")
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, aktorId)
                );
        }
        public static SelveXmlMessage getIds() {
            return new SelveXmlMethodCall()
                .withMethodName("selve.GW.device.getIDs");
        }
        public static SelveXmlMessage setFunction(int aktorId, int function) {
            Validator.validateCommeoAktorId(aktorId);
            return new SelveXmlMethodCall()
                .withMethodName("selve.GW.device.setFunction")
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, aktorId),
                    new SelveXmlMethodParameter(ParameterType.INT, function)
                );
        }
        public static SelveXmlMessage setLabel(int aktorId, String label) {
            Validator.validateCommeoAktorId(aktorId);
            return new SelveXmlMethodCall()
                .withMethodName("selve.GW.device.setLabel")
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, aktorId),
                    new SelveXmlMethodParameter(ParameterType.STRING, label)
                );
        }
        public static SelveXmlMessage setType(int aktorId, int type) {
            Validator.validateCommeoAktorId(aktorId);
            return new SelveXmlMethodCall()
                .withMethodName("selve.GW.device.setType")
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, aktorId),
                    new SelveXmlMethodParameter(ParameterType.INT, type)
                );
        }
        public static SelveXmlMessage delete(int aktorId) {
            Validator.validateCommeoAktorId(aktorId);
            return new SelveXmlMethodCall()
                .withMethodName("selve.GW.device.delete")
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, aktorId)
                );
        }
    }

    public static class Command {
        public static SelveXmlMessage device(int aktorId, int command, int type, int parameter) {
            Validator.validateCommeoAktorId(aktorId);
            return new SelveXmlMethodCall()
                .withMethodName("selve.GW.command.device")
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, aktorId),
                    new SelveXmlMethodParameter(ParameterType.INT, command),
                    new SelveXmlMethodParameter(ParameterType.INT, type),
                    new SelveXmlMethodParameter(ParameterType.INT, parameter)
                );
        }
        public static SelveXmlMessage group(int groupId, int command, int type, int parameter) {
            Validator.validateCommeoGroupId(groupId);
            return new SelveXmlMethodCall()
                .withMethodName("selve.GW.command.group")
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, groupId),
                    new SelveXmlMethodParameter(ParameterType.INT, command),
                    new SelveXmlMethodParameter(ParameterType.INT, type),
                    new SelveXmlMethodParameter(ParameterType.INT, parameter)
                );
        }
    }
}
