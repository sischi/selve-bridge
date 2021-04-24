package com.sischi.selvebridge.gateway.xml.specification;

import java.util.List;

import com.sischi.selvebridge.configuration.properties.ParamaterInformationConfig;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameter;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterInt;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterType;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.util.HasLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessagePostProcessor implements HasLogger {

    @Autowired
    private ParamaterInformationConfig parameterInformationConfig;

    public void postProcessMessage(SelveXmlMessage message) {
        if(message == null || message.getParameters() == null || message.getParameters().isEmpty()) {
            return;
        }

        List<ParameterInformation> paramInfoList = parameterInformationConfig.getParameterInfo(message.getMethodName(), message.getType());
        if (paramInfoList == null) {
            getLogger().info("no parameter information found for method name '{}' and message type '{}'", message.getMethodName(), message.getType().name());
            return;
        }

        if(message.getParameters() != null) {

            int index = 0;
            for (SelveMethodParameter<?> param : message.getParameters()) {
                ParameterInformation paramInfo = paramInfoList.get(index);
                if(paramInfo == null) {
                    continue;
                }

                // apply friendly parameter name
                param.setFriendlyName(paramInfo.getFriendlyName());

                // try to parse friendly value
                try {
                    if(paramInfo.getFriendlyValues() != null && paramInfo.getFriendlyValues().size() > 0 && param.getType() == SelveMethodParameterType.INT) {
                        String friendlyValue = paramInfo.getFriendlyValues().get(((SelveMethodParameterInt) param).getValue());
                        param.setFriendlyValue(friendlyValue);
                    }
                } catch(Exception e) {
                    getLogger().warn("something went wrong parsing the friendly value for parameter '{}' of method name '{}' and message type '{}'!", param, message.getMethodName(), message.getType().name(), e);
                }
                index++;
            }
        }
    }


}
