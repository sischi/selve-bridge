package com.sischi.selvebridge.core.entities.enumerations;

public enum DeviceState {
    UNKNOWN(0),
    STOPPED(1),
    DRIVE_UP(2),
    DRIVE_DOWN(3);

    private int value;

    private DeviceState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DeviceState parse(int value) {
        for(DeviceState state : DeviceState.values()) {
            if(state.getValue() == value) {
                return state;
            }
        }
        return null;
    }
}
