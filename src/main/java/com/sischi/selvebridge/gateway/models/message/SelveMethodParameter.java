package com.sischi.selvebridge.gateway.models.message;

import com.fasterxml.jackson.annotation.JsonInclude;

public abstract class SelveMethodParameter<T> {
    protected SelveMethodParameterType type;
    protected T value;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String friendlyName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String friendlyValue;

    public SelveMethodParameter() {}

    public SelveMethodParameterType getType() {
        return type;
    }
    public void setType(SelveMethodParameterType type) {
        this.type = type;
    }
    public String getFriendlyName() {
        return friendlyName;
    }
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }
    public String getFriendlyValue() {
        return friendlyValue;
    }
    public void setFriendlyValue(String friendlyValue) {
        this.friendlyValue = friendlyValue;
    }
    public T getValue() {
        return value;
    }
    public void setValue(T value) {
        this.value = value;
    }
    
    public String toXmlString() {
        return "<"+ type.getXmlTag() +">"+ value.toString() +"</"+ type.getXmlTag() +">";
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +"=["+
                "type='"+ type +"', "+
                "value='"+ value.toString() +"', "+
                "friendlyName='"+ friendlyName +"', "+
                "friendlyValue='"+ friendlyValue +"'"+
            "]";
    }
    
}
