package com.sischi.selvebridge.gateway;

import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodResponse;

public interface SelveXmlMessageHandler {
    void onMethodResponse(SelveXmlMethodResponse response);
	void onMethodCall(SelveXmlMethodCall call);
}
