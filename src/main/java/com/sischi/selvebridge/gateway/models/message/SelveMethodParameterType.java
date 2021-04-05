package com.sischi.selvebridge.gateway.models.message;

public enum SelveMethodParameterType {
    INT("int"),
    STRING("string"),
    BASE64("base64");

    String xmlTag = "";

    SelveMethodParameterType(String xmlTag) {
        this.xmlTag = xmlTag;
    }

    public String getXmlTag() {
        return xmlTag;
    }

    public static SelveMethodParameterType parse(String text) {
        for(SelveMethodParameterType type : SelveMethodParameterType.values()) {
            if(type.getXmlTag().equals(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("no parameter type found for value '"+ text +"'!");
    }
}
