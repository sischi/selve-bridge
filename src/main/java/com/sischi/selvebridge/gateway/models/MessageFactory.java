package com.sischi.selvebridge.gateway.models;

import com.sischi.selvebridge.gateway.models.enums.MethodNames;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodParameter;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodParameter.ParameterType;
import com.sischi.selvebridge.util.Validator;

public class MessageFactory {

    public static class Service {
        public static SelveXmlMessage ping() {
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Service.PING);
        }
        public static SelveXmlMessage getState() {
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Service.GET_STATE);
        }
        public static SelveXmlMessage getVersion() {
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Service.GET_VERSION);
        }
        public static SelveXmlMessage reset() {
			return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Service.RESET);
		}
		public static SelveXmlMessage factoryReset() {
			return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Service.FACTORY_RESET);
		}
        public static SelveXmlMessage getLed() {
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Service.GET_LED);
        }
        public static SelveXmlMessage setLed(int ledMode) {
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Service.SET_LED)
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, ledMode)
                );
        }
    }


    public static class Device {
        public static SelveXmlMessage scanStart() {
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.SCAN_START);
        }
        public static SelveXmlMessage scanStop() {
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.SCAN_STOP);
        }
        public static SelveXmlMessage scanResult() {
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.SCAN_RESULT);
        }
        public static SelveXmlMessage getInfo(int deviceId) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.GET_INFO)
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, deviceId)
                );
        }
        public static SelveXmlMessage save(int deviceId) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.SAVE)
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, deviceId)
                );
        }
        public static SelveXmlMessage getValues(int deviceId) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.GET_VALUES)
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, deviceId)
                );
        }
        public static SelveXmlMessage getIds() {
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.GET_IDS);
        }
        public static SelveXmlMessage setFunction(int deviceId, int function) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.SET_FUNCTION)
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, deviceId),
                    new SelveXmlMethodParameter(ParameterType.INT, function)
                );
        }
        public static SelveXmlMessage setLabel(int deviceId, String label) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.SET_LABEL)
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, deviceId),
                    new SelveXmlMethodParameter(ParameterType.STRING, label)
                );
        }
        public static SelveXmlMessage setType(int deviceId, int type) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.SET_TYPE)
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, deviceId),
                    new SelveXmlMethodParameter(ParameterType.INT, type)
                );
        }
        public static SelveXmlMessage delete(int deviceId) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.DELETE)
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, deviceId)
                );
        }
    }

    public static class Command {
        public static SelveXmlMessage device(int deviceId, int command, int type) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Command.DEVICE)
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, deviceId),
                    new SelveXmlMethodParameter(ParameterType.INT, command),
                    new SelveXmlMethodParameter(ParameterType.INT, type)
                );
        }
        public static SelveXmlMessage device(int deviceId, int command, int type, int parameter) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Command.DEVICE)
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, deviceId),
                    new SelveXmlMethodParameter(ParameterType.INT, command),
                    new SelveXmlMethodParameter(ParameterType.INT, type),
                    new SelveXmlMethodParameter(ParameterType.INT, parameter)
                );
        }
        public static SelveXmlMessage group(int groupId, int command, int type, int parameter) {
            Validator.validateCommeoGroupId(groupId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Command.GROUP)
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.INT, groupId),
                    new SelveXmlMethodParameter(ParameterType.INT, command),
                    new SelveXmlMethodParameter(ParameterType.INT, type),
                    new SelveXmlMethodParameter(ParameterType.INT, parameter)
                );
        }
    }
}
