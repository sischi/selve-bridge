package com.sischi.selvebridge.gateway.models.commeo;

import java.util.List;

import com.sischi.selvebridge.gateway.models.enums.DeviceState;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameter;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterInt;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodResponse;
import com.sischi.selvebridge.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommeoDeviceStateFactory {

    public static Logger getLogger() {
		return LoggerFactory.getLogger(CommeoDeviceStateFactory.class);
	}
    
    public static CommeoDeviceState parseFromDeviceStateResponse(SelveXmlMethodResponse response) {
        if(response == null) {
            getLogger().warn("expected given response to be not null! nothing to parse");
            return null;
        }

        if(response.isError()) {
            getLogger().warn("the response that should be parsed is an error response: '{}'", response);
            return null;
        }
    
        CommeoDeviceState deviceState = new CommeoDeviceState();

        List<SelveMethodParameter<?>> params = response.getParameters();

        deviceState.setDeviceId((Integer) params.get(0).getValue());
        deviceState.setState(DeviceState.parse((Integer) params.get(1).getValue()));
        deviceState.setPosition(Utils.positionToPercentage((Integer) params.get(2).getValue()));
        deviceState.setTargetPosition(Utils.positionToPercentage((Integer) params.get(3).getValue()));

        return deviceState;
    }


    public static CommeoDeviceState parseFromDeviceStateEvent(SelveXmlMethodCall call) {
        if(call == null) {
            getLogger().warn("expected given method call to be not null! nothing to parse");
            return null;
        }

        CommeoDeviceState deviceState = new CommeoDeviceState();

        List<SelveMethodParameter<?>> params = call.getParameters();

        deviceState.setDeviceId(((SelveMethodParameterInt) params.get(0)).getValue());
        deviceState.setState(DeviceState.parse(((SelveMethodParameterInt) params.get(1)).getValue()));
        deviceState.setPosition(Utils.positionToPercentage(((SelveMethodParameterInt) params.get(2)).getValue()));
        deviceState.setTargetPosition(Utils.positionToPercentage(((SelveMethodParameterInt) params.get(3)).getValue()));

        return deviceState;
    }

}
