package com.sischi.selvebridge.gateway.xml.specification;

import java.util.ArrayList;
import java.util.List;

import com.sischi.selvebridge.gateway.models.message.MessageType;

public class MethodInformation {

    private String name;
    private MessageType type;
    private int parameterCount;
    private List<String> parameterNames;
    private List<Class<?>> parameterTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MethodInformation withName(String name) {
        setName(name);
        return this;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MethodInformation withType(MessageType type) {
        setType(type);
        return this;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public void setParameterCount(int parameterCount) {
        this.parameterCount = parameterCount;
    }

    public MethodInformation withParameterCount(int parameterCount) {
        setParameterCount(parameterCount);
        return this;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(List<String> parameterNames) {
        this.parameterNames = parameterNames;
    }

    public void addParameterName(String parameterName) {
        if(parameterNames == null) {
            parameterNames = new ArrayList<>();
        }
        parameterNames.add(parameterName);
    }

    public MethodInformation withParameterNames(String... parameterNames) {
        for(String name : parameterNames) {
            addParameterName(name);
        }
        return this;
    }

    public List<Class<?>> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<Class<?>> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void addParameterType(Class<?> parameterType) {
        if(parameterTypes == null) {
            parameterTypes = new ArrayList<>();
        }
        parameterTypes.add(parameterType);
    }

    public MethodInformation withParameterTypes(Class<?>... parameterTypes) {
        for(Class<?> type : parameterTypes) {
            addParameterType(type);
        }
        return this;
    }
    
}
