package com.sischi.selvebridge.core.entities.control;

import com.sischi.selvebridge.core.entities.enumerations.CommeoCommand;

public class CommeoCommandPayload {

    private CommeoCommand command;
    private Integer value;

    public CommeoCommand getCommand() {
        return command;
    }

    public void setCommand(CommeoCommand command) {
        this.command = command;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "[command="+ getCommand() +", value="+ getValue() +"]";
    }
}
