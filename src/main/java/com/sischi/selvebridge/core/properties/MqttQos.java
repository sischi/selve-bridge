package com.sischi.selvebridge.core.properties;

public enum MqttQos {

    AT_MOST_ONCE(0),
    AT_LEAST_ONCE(1),
    EXACTLY_ONCE(2);

    private int qos;

    private MqttQos(int qos) {
        this.qos = qos;
    }

    public static MqttQos parse(int qos) {
        switch(qos) {
            case 0: return AT_MOST_ONCE;
            case 1: return AT_LEAST_ONCE;
            case 2: return EXACTLY_ONCE;
            default: throw new IllegalArgumentException("no '"+ MqttQos.class.getSimpleName() +"' found for value '"+ qos +"'!");
        }
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }
    
}
