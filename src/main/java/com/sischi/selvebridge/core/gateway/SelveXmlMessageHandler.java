package com.sischi.selvebridge.core.gateway;

import com.sischi.selvebridge.core.entities.message.SelveXmlMethodCall;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodResponse;

public interface SelveXmlMessageHandler {
    void onMethodResponse(SelveXmlMethodResponse response);
	void onMethodCall(SelveXmlMethodCall call);
}
