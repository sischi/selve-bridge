package com.sischi.selvebridge.gateway.models.commeo;

public class CommeoCommandPayload {

    private CommeoCommand command;
    private Integer value;
    private CommeoCommandTargetType targetType;
    private Object target;

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

    public CommeoCommandTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(CommeoCommandTargetType targetType) {
        this.targetType = targetType;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "[command="+ getCommand() +", value="+ getValue() +", targetType="+ getTargetType() +", target="+ getTarget() +"]";
    }

    
}
