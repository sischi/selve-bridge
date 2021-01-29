package com.sischi.selvebridge.mqtt.entity;

import com.sischi.selvebridge.core.xml.enumerations.CommeoCommand;

public class MqttCommandPayload {

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
