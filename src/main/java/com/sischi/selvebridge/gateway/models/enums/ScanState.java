package com.sischi.selvebridge.gateway.models.enums;

public enum ScanState {
    IDLE(0),
    RUN(1),
    VERIFY(2),
    END_SUCCESS(3),
    END_FAILED(4);

    private int value;

    private ScanState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ScanState parse(int value) {
        for(ScanState state : ScanState.values()) {
            if(state.getValue() == value) {
                return state;
            }
        }
        return null;
    }
}
