package com.sischi.selvebridge.gateway.models.commeo;

import java.util.ArrayList;
import java.util.List;

public class CommeoCommandPayload {

    private CommeoCommand command;
    private Integer value;
    private CommeoCommandTargetType targetType;
    private List<Integer> target = new ArrayList<>();

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

    public List<Integer> getTarget() {
        return target;
    }

    public void setTarget(List<Integer> target) {
        this.target = target;
    }

    public void addTarget(Integer... targets) {
        this.target = new ArrayList<>();
        for(Integer target : targets) {
            if(target != null) {
                this.target.add(target);
            }
        }
    }

    @Override
    public String toString() {
        return "[command="+ getCommand() +", value="+ getValue() +", targetType="+ getTargetType() +", target="+ getTarget() +"]";
    }

    
}
