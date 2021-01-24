package com.sischi.selvebridge.core.util;

public class Validator {
    

    public static void validateCommeoAktorId(int aktorId) {
        if(aktorId < 0 || aktorId > 63) throw new IllegalArgumentException("aktorId must be between 0 and 63, but found '"+ aktorId +"'");
    }

	public static void validateCommeoGroupId(int groupId) {
        if(groupId < 0 || groupId > 31) throw new IllegalArgumentException("groupId must be between 0 and 31, but found '"+ groupId +"'");
	}

}
