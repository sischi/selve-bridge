package com.sischi.selvebridge.util;

public class Validator {
    

    public static void validateCommeoDeviceId(int deviceId) {
        if(deviceId < 0 || deviceId > 63) throw new IllegalArgumentException("deviceId must be between 0 and 63, but found '"+ deviceId +"'");
    }

	public static void validateCommeoGroupId(int groupId) {
        if(groupId < 0 || groupId > 31) throw new IllegalArgumentException("groupId must be between 0 and 31, but found '"+ groupId +"'");
	}

}
