package com.sischi.selvebridge.gateway.models.commeo;



public enum CommeoCommandType {
    FORCED(0),
    MANUAL(1),
    TIME(2),
    GLASS(3);

    private int value;

    private CommeoCommandType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CommeoCommandType parse(int value) {
        for(CommeoCommandType type : CommeoCommandType.values()) {
            if(type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}


