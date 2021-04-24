package com.sischi.selvebridge.configuration.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sischi.selvebridge.gateway.models.message.MessageType;
import com.sischi.selvebridge.gateway.xml.specification.ParameterInformation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "parameter-information-config")
public class ParamaterInformationConfig {
    private Map<String, List<ParameterInformation>> methodCalls = new HashMap<>();
    private Map<String, List<ParameterInformation>> methodResponses = new HashMap<>();

    
    public Map<String, List<ParameterInformation>> getMethodCalls() {
        return methodCalls;
    }

    public void setMethodCalls(Map<String, List<ParameterInformation>> methodCalls) {
        this.methodCalls = methodCalls;
    }

    public Map<String, List<ParameterInformation>> getMethodResponses() {
        return methodResponses;
    }

    public void setMethodResponses(Map<String, List<ParameterInformation>> methodResponses) {
        this.methodResponses = methodResponses;
    }


    public List<ParameterInformation> getParameterInfo(String methodName, MessageType messageType) {
        switch (messageType) {
            case METHOD_CALL:
                return methodCalls != null ? methodCalls.get(methodName) : null;    
            case METHOD_RESPONSE:
                return methodResponses != null ? methodResponses.get(methodName) : null;
            default:
                return null;
        }
    }


    
}
