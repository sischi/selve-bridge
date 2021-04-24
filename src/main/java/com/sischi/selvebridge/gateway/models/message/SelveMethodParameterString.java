package com.sischi.selvebridge.gateway.models.message;

public class SelveMethodParameterString extends SelveMethodParameter<String> {
    
    public SelveMethodParameterString() {
        this.type = SelveMethodParameterType.STRING;
    }

    public SelveMethodParameterString(String value) {
        this();
        this.value = value;
    }

}
