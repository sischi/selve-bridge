package com.sischi.selvebridge.core.entities.commeo;

import com.sischi.selvebridge.core.entities.enumerations.DayMode;
import com.sischi.selvebridge.core.entities.enumerations.DeviceAvailability;
import com.sischi.selvebridge.core.entities.enumerations.DeviceState;
import com.sischi.selvebridge.core.entities.enumerations.DeviceType;

public class CommeoDeviceState {

    private int deviceId;
    private String label;

    private DeviceAvailability availability;
    private DeviceType type;
    
    private DeviceState state;
    private Integer position;
    private Integer targetPosition;
    private DayMode dayMode;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DeviceAvailability getAvailability() {
        return availability;
    }

    public void setAvailability(DeviceAvailability availability) {
        this.availability = availability;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public DeviceState getState() {
        return state;
    }

    public void setState(DeviceState state) {
        this.state = state;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(Integer targetPosition) {
        this.targetPosition = targetPosition;
    }

    public DayMode getDayMode() {
        return dayMode;
    }

    public void setDayMode(DayMode dayMode) {
        this.dayMode = dayMode;
    }
    
    
    

}
