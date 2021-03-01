package com.sischi.selvebridge.gateway.xml.specification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.sischi.selvebridge.gateway.models.enums.DeviceType;
import com.sischi.selvebridge.gateway.models.enums.GetInfoState;
import com.sischi.selvebridge.gateway.models.enums.MethodNames;
import com.sischi.selvebridge.gateway.models.enums.ScanState;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodParameter;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodParameter.ParameterType;
import com.sischi.selvebridge.util.HasLogger;
import com.sischi.selvebridge.util.Utils;

import org.springframework.stereotype.Component;

@Component
public class MessagePostProcessor implements HasLogger {

    private Map<String, MethodInformation> additionalMethodInformation = new HashMap<>();

    @PostConstruct
    protected void init() {
        registerMethodInformation();
    }

    public void postProcessMessage(SelveXmlMessage message) {
        MethodInformation methodInfo = additionalMethodInformation.get(message.getMethodName());
        if (methodInfo == null) {
            getLogger().debug("no method information found for method name '{}' and message type '{}'",
                    message.getMethodName(), message.getType().name());
            return;
        }

        if(message.getParameters().size() == methodInfo.getParameterCount()) {
            List<String> paramNames = methodInfo.getParameterNames();
            List<Class<?>> paramTypes = methodInfo.getParameterTypes();

            int index = 0;
            for (SelveXmlMethodParameter param : message.getParameters()) {

                // apply friendly parameter name
                param.setFriendlyName(paramNames.get(index));

                try {
                    // apply friendly parameter value
                    param.setFriendlyValue(parseFriendlyValue(param, paramTypes.get(index)));
                } catch (Exception e) {
                    getLogger().warn("something went wrong parsing the friendly value for parameter '{}'!", param, e);
                }
                index++;
            }
        }
    }

    public String parseFriendlyValue(SelveXmlMethodParameter param, Class<?> friendlyParamType)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String friendlyValue = null;
        
        if (friendlyParamType != null) {
            if(friendlyParamType.isEnum()) {
                Method parseMethod = friendlyParamType.getMethod("parse", int.class);
                Object obj = parseMethod.invoke(null, param.getValue());
                Method nameMethod = friendlyParamType.getMethod("name");
                friendlyValue = (String) nameMethod.invoke(obj);
            }
            else {
                getLogger().warn("unsupported conversion of parameter '{}' to friendly value by using class '{}'!", param, friendlyParamType.getSimpleName());
            }
        }
        return friendlyValue;
    }



    protected void registerMethodInformation() {
        MethodInformation methodInfo = null;

        methodInfo = new MethodInformation()
            .withName(MethodNames.DEVICE_GETINFO)
            .withParameterCount(5)
            .withParameterNames(
                "device id",
                "rf adress",
                "device name",
                "configuration",
                "state"
            )
            .withParameterTypes(
                null,
                null,
                null,
                DeviceType.class,
                GetInfoState.class
            );
        
        additionalMethodInformation.put(methodInfo.getName(), methodInfo);

        methodInfo = new MethodInformation()
            .withName(MethodNames.DEVICE_SCANRESULT)
            .withParameterCount(3)
            .withParameterNames(
                "status",
                "number of new devices",
                "device mask"
            )
            .withParameterTypes(
                ScanState.class,
                null,
                null
            );
        
        additionalMethodInformation.put(methodInfo.getName(), methodInfo);
    }

}
