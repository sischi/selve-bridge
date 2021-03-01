package com.sischi.selvebridge.gateway.models.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sischi.selvebridge.util.Utils;

public class SelveXmlMethodParameter {
    private ParameterType type = null;
    private Object value = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String friendlyName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String friendlyValue;

    public SelveXmlMethodParameter() {}    

    public SelveXmlMethodParameter(ParameterType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
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

    public void parseValue(String strValue) {
        switch (type) {
            case INT:
                setValue(Integer.parseInt(strValue));
                break;
            case BASE64:
                setValue(strValue);
                setFriendlyValue(Utils.base64ToBinary(strValue));
                setFriendlyName("device mask");
                break;
            case STRING:
            default:
                setValue(strValue);
                break;
        }
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

    public enum ParameterType {
        INT("int"),
        STRING("string"),
        BASE64("base64");

        String xmlTag = "";

        ParameterType(String xmlTag) {
            this.xmlTag = xmlTag;
        }

        public String getXmlTag() {
            return xmlTag;
        }

        public static ParameterType parse(String text) {
            for(ParameterType type : ParameterType.values()) {
                if(type.getXmlTag().equals(text)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("no parameter type found for value '"+ text +"'!");
        }
    }

    public String toXmlString() {
        return "<"+ type.getXmlTag() +">"+ value.toString() +"</"+ type.getXmlTag() +">";
    }

    

    
}
