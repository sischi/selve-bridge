package com.sischi.selvebridge.gateway.models;

import java.util.List;

import com.sischi.selvebridge.gateway.models.enums.MethodNames;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterBase64;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterInt;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterString;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;
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
                    new SelveMethodParameterInt(ledMode)
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
                    new SelveMethodParameterInt(deviceId)
                );
        }
        public static SelveXmlMessage save(int deviceId) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.SAVE)
                .withParameter(
                    new SelveMethodParameterInt(deviceId)
                );
        }
        public static SelveXmlMessage getValues(int deviceId) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.GET_VALUES)
                .withParameter(
                    new SelveMethodParameterInt(deviceId)
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
                    new SelveMethodParameterInt(deviceId),
                    new SelveMethodParameterInt(function)
                );
        }
        public static SelveXmlMessage setLabel(int deviceId, String label) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.SET_LABEL)
                .withParameter(
                    new SelveMethodParameterInt(deviceId),
                    new SelveMethodParameterString(label)
                );
        }
        public static SelveXmlMessage setType(int deviceId, int type) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.SET_TYPE)
                .withParameter(
                    new SelveMethodParameterInt(deviceId),
                    new SelveMethodParameterInt(type)
                );
        }
        public static SelveXmlMessage delete(int deviceId) {
            Validator.validateCommeoDeviceId(deviceId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Device.DELETE)
                .withParameter(
                    new SelveMethodParameterInt(deviceId)
                );
        }
    }



    public static class Group {
        public static SelveXmlMessage read(int groupId) {
            Validator.validateCommeoGroupId(groupId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Group.READ)
                .withParameter(
                    new SelveMethodParameterInt(groupId)
                );
        }

        public static SelveXmlMessage write(int groupId, List<Integer> ids, String name) {
            Validator.validateCommeoGroupId(groupId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Group.READ)
                .withParameter(
                    new SelveMethodParameterInt(groupId),
                    SelveMethodParameterBase64.ofIdList(ids),
                    new SelveMethodParameterString(name)
                );
        }

        public static SelveXmlMessage getIds(int groupId) {
            Validator.validateCommeoGroupId(groupId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Group.READ)
                .withParameter(
                    new SelveMethodParameterInt(groupId)
                );
        }

        public static SelveXmlMessage delete(int groupId) {
            Validator.validateCommeoGroupId(groupId);
            return new SelveXmlMethodCall()
                .withMethodName(MethodNames.Group.READ)
                .withParameter(
                    new SelveMethodParameterInt(groupId)
                );
        }
    }




    public static class Command {
        public static SelveXmlMessage device(int deviceId, int command, int type, Integer parameter) {
            Validator.validateCommeoDeviceId(deviceId);
            SelveXmlMessage message = new SelveXmlMethodCall()
                .withMethodName(MethodNames.Command.DEVICE)
                .withParameter(
                    new SelveMethodParameterInt(deviceId),
                    new SelveMethodParameterInt(command),
                    new SelveMethodParameterInt(type)
                );
            if(parameter != null) {
                message.addParamater(new SelveMethodParameterInt(parameter));
            }
            
            return message;
        }
        public static SelveXmlMessage group(int groupId, int command, int type, Integer parameter) {
            Validator.validateCommeoGroupId(groupId);
            SelveXmlMessage message = new SelveXmlMethodCall()
                .withMethodName(MethodNames.Command.GROUP)
                .withParameter(
                    new SelveMethodParameterInt(groupId),
                    new SelveMethodParameterInt(command),
                    new SelveMethodParameterInt(type)
                );
            if(parameter != null) {
                message.addParamater(new SelveMethodParameterInt(parameter));
            }
            return message;
        }
    }
}
