package com.sischi.selvebridge.gateway.models.enums;


public enum GetInfoState {
    UNUSED(0),
    USED(1),
    TEMPORARILY_USED(2),
    DELETED(3);

    private int value;

    private GetInfoState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GetInfoState parse(int value) {
        for(GetInfoState state : GetInfoState.values()) {
            if(state.getValue() == value) {
                return state;
            }
        }
        return null;
    }

}
