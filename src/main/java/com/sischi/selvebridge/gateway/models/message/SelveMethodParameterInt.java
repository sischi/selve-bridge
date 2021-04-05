package com.sischi.selvebridge.gateway.models.message;

public class SelveMethodParameterInt extends SelveMethodParameter<Integer> {

    public SelveMethodParameterInt() {
        this.type = SelveMethodParameterType.INT;
    }

    public SelveMethodParameterInt(Integer value) {
        this();
        this.value = value;
    }
    

}
