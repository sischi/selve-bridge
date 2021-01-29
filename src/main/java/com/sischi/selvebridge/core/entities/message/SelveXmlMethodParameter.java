package com.sischi.selvebridge.core.entities.message;




public class SelveXmlMethodParameter {
    private ParameterType type = null;
    private Object value = null;

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

    public void parseValue(String strValue) {
        switch (type) {
            case INT:
                setValue(Integer.parseInt(strValue));
                break;
            case STRING:
            case BASE64:
            default:
                setValue(strValue);
                break;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +"=["+
            "type="+ type +", "+
            "value="+ value.toString() +
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
