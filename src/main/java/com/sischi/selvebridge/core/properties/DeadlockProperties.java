package com.sischi.selvebridge.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "selvebridge.deadlock")
public class DeadlockProperties {
    
    private int threshold = 60;
    private int watchdogInterval = 30;

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getWatchdogInterval() {
        return watchdogInterval;
    }

    public void setWatchdogInterval(int watchdogInterval) {
        this.watchdogInterval = watchdogInterval;
    }

    

}
