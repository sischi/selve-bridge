package com.sischi.selvebridge.gateway.models.enums;

public enum DeviceType {
    UNKNOWN(0),
    SHUTTER(1),
    JALOUSIE(2),
    AWNING(3),
    SWITCH_ACTUATOR(4),
    DIMMER(5),
    NIGHTLIGHT_ACTUATOR(6),
    DAWNLIGHT_ACTUATOR(7),
    HEATING(8),
    REFRIGERATOR(9),
    SWITCH_ACTUATOR_DAY(10),
    GATEWAY(11);

    private int value;

    private DeviceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DeviceType parse(int value) {
        for(DeviceType state : DeviceType.values()) {
            if(state.getValue() == value) {
                return state;
            }
        }
        return null;
    }
}
