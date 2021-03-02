package com.sischi.selvebridge.gateway.models.enums;

public class MethodNames {

    public static class Service {
        public static final String PING = "selve.GW.service.ping";
        public static final String GET_STATE = "selve.GW.service.getState";
        public static final String GET_VERSION = "selve.GW.service.getVersion";
        public static final String RESET = "selve.GW.service.reset";
        public static final String FACTORY_RESET = "selve.GW.service.factoryReset";
        public static final String GET_LED = "selve.GW.service.getLED";
        public static final String SET_LED = "selve.GW.service.setLED";
    }
    
    public static class Device {
        public static final String SCAN_START = "selve.GW.device.scanStart";
        public static final String SCAN_STOP = "selve.GW.device.scanStop";
        public static final String SCAN_RESULT = "selve.GW.device.scanResult";
        public static final String SAVE = "selve.GW.device.save";
        public static final String GET_IDS = "selve.GW.device.getIDs";
        public static final String GET_INFO = "selve.GW.device.getInfo";
        public static final String GET_VALUES = "selve.GW.device.getValues";
        public static final String SET_FUNCTION = "selve.GW.device.setFunction";
        public static final String SET_LABEL = "selve.GW.device.setLabel";
        public static final String SET_TYPE = "selve.GW.device.setType";
        public static final String DELETE = "selve.GW.device.delete";
        public static final String WRITE_MANUAL = "selve.GW.device.writeManual";
    }

    public static class Command {
        public static final String DEVICE = "selve.GW.command.device";
        public static final String GROUP = "selve.GW.command.group";
        public static final String RESULT = "selve.GW.command.result";
    }

    public static class Event {
        public static final String DEVICE = "selve.GW.event.device";
        public static final String LOG = "selve.GW.event.log";
    }
}
