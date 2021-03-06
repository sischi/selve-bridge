package com.sischi.selvebridge.gateway.models.message;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


public class SelveXmlMessage {

    public static final String XML_TAG_METHOD_NAME = "methodName";
    public static final String XML_TAG_METHOD_PARAMATER = "array";
    public static final String XML_TAG_ERROR = "fault";

    protected MessageType type;

    @JacksonXmlProperty(localName = "methodName")
    protected String methodName;

    @JacksonXmlElementWrapper(useWrapping = false)
    protected List<SelveMethodParameter<?>> parameters;

    protected SelveXmlError error;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public SelveXmlMessage withType(MessageType type) {
        setType(type);
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public SelveXmlMessage withMethodName(String methodName) {
        setMethodName(methodName);
        return this;
    }

    public List<SelveMethodParameter<?>> getParameters() {
        return parameters;
    }

    public void setParameters(List<SelveMethodParameter<?>> parameters) {
        this.parameters = parameters;
    }

    public void addParamater(SelveMethodParameter<?> parameter) {
        if(parameters == null) {
            parameters = new ArrayList<>();
        }
        parameters.add(parameter);
    }

    public SelveXmlMessage withParameter(SelveMethodParameter<?>... parameters) {
        for(SelveMethodParameter<?> param : parameters) {
            addParamater(param);
        }
        return this;
    }

    public boolean isError() {
        return error != null;
    }

    public SelveXmlError getError() {
        return error;
    }

    public void setError(SelveXmlError error) {
        this.error = error;
    }

    public SelveXmlMessage withError(SelveXmlError error) {
        setError(error);
        return this;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +"=[\n"+
            "type="+ type +",\n "+
            "methodName="+ methodName +",\n "+
            "error="+ error +",\n "+
            "parameters={\n"+
                (parameters != null
                ? parameters.stream().map(param -> param.toString()).collect(Collectors.joining(",\n "))
                : "")
                +"}\n"+
            "]";
    }
}
