package com.sischi.selvebridge.util;

import java.util.List;

public class Validator {
    

    public static void validateCommeoDeviceId(int deviceId) {
        if(deviceId < 0 || deviceId > 63) throw new IllegalArgumentException("deviceId must be between 0 and 63, but found '"+ deviceId +"'");
    }

    public static void validateCommeoDeviceIds(List<Integer> deviceIds) {
        if(deviceIds == null) throw new IllegalArgumentException("list of device ids should not be null!");
        for(Integer deviceId : deviceIds) {
            if(deviceId < 0 || deviceId > 63) throw new IllegalArgumentException("deviceId must be between 0 and 63, but found '"+ deviceId +"'");
        }
    }

	public static void validateCommeoGroupId(int groupId) {
        if(groupId < 0 || groupId > 31) throw new IllegalArgumentException("groupId must be between 0 and 31, but found '"+ groupId +"'");
	}

    public static void validateBinaryMask(String binary) {
        if(binary == null) throw new IllegalArgumentException("device mask cannot be 'null'!");
        if(!binary.matches("(0|1)+") || binary.length() % 8 != 0) throw new IllegalArgumentException("device mask '"+ binary +"' should only contain '0' and '1' and should be a multiple of 8 bit long!"); 
    }

    public static void validateBinaryDeviceIdMask(String binary) {
        if(binary == null) throw new IllegalArgumentException("device mask cannot be 'null'!");
        if(!binary.matches("(0|1){64}$")) throw new IllegalArgumentException("device mask '"+ binary +"' should only contain '0' and '1' and should not be longer than 64 bit!"); 
    }

    public static void validateBinaryGroupIdMask(String binary) {
        if(binary == null) throw new IllegalArgumentException("group mask cannot be 'null'!");
        if(!binary.matches("(0|1){32}$")) throw new IllegalArgumentException("group mask '"+ binary +"' should only contain '0' and '1' and should not be longer than 32 bit!"); 
    }

}
