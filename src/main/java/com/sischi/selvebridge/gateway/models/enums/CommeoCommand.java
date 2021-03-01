package com.sischi.selvebridge.gateway.models.enums;

public enum CommeoCommand {
    STOP(0),
    DRIVE_UP(1),
    DRIVE_DOWN(2),
    DRIVE_POS1(3),
    SAVE_POS1(4),
    DRIVE_POS2(5),
    SAVE_POS2(6),
    DRIVE_POS(7),
    STEP_UP(8),
    STEP_DOWN(9),
    AUTO_ON(10),
    AUTO_OFF(11);

    private int value;

    private CommeoCommand(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CommeoCommand parse(int value) {
        for(CommeoCommand command : CommeoCommand.values()) {
            if(command.getValue() == value) {
                return command;
            }
        }
        return null;
    }
}
