package com.sischi.selvebridge.gateway.models.message;

public enum MessageType {
    METHOD_CALL("methodCall"),
    METHOD_RESPONSE("methodResponse");

    String xmlTag = "";

    MessageType(String xmlTag) {
        this.xmlTag = xmlTag;
    }

    public String getXmlTag() {
        return xmlTag;
    }

    public static MessageType parse(String text) {
        for(MessageType type : MessageType.values()) {
            if(type.getXmlTag().equals(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("no message type found for value '"+ text +"'!");
    }
}
