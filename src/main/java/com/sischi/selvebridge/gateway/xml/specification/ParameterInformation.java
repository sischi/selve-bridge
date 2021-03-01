package com.sischi.selvebridge.gateway.xml.specification;

import java.util.List;

public class ParameterInformation {

    private String friendlyName;
    private List<String> friendlyValues = null;

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String name) {
        this.friendlyName = name;
    }

    public List<String> getFriendlyValues() {
        return friendlyValues;
    }

    public void setFriendlyValues(List<String> friendlyValues) {
        this.friendlyValues = friendlyValues;
    }
    
}
