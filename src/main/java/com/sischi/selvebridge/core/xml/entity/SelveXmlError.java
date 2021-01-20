package com.sischi.selvebridge.core.xml.entity;

public class SelveXmlError {

    private Integer code;
    private String description;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public SelveXmlError withCode(Integer code) {
        setCode(code);
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SelveXmlError withDescription(String description) {
        setDescription(description);
        return this;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +"=["+
            "code="+ code +", "+
            "description="+ description +
            "]";
    }
}
