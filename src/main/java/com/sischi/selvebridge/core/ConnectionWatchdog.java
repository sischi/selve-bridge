package com.sischi.selvebridge.core;

import com.sischi.selvebridge.core.util.HasLogger;

public class ConnectionWatchdog implements HasLogger {

    private String name;
    private Thread watchdog = null;
    private int interval;

    private ConnectionWatchdogHandler handler;

    public interface ConnectionWatchdogHandler {
        boolean checkConnection();
        void handleLostConnection();
    }

    public ConnectionWatchdog(String name, int interval, ConnectionWatchdogHandler handler) {
        this.name = name;
        this.interval = interval;
        this.handler = handler;
    }

    public void start() {
        if (interval <= 0) {
            getLogger().warn("connection watchdog is disabled due to corresponding watchdog interval is set to '"+ interval +"'!");
            return;
        }

        if(handler == null) {
            getLogger().error("no connection watchdog handler found!");
            return;
        }

        if (watchdog == null || !watchdog.isAlive()) {
            watchdog = new Thread(() -> {
                getLogger().info("connection watchdog started ...");
                while (handler.checkConnection()) {
                    getLogger().info("the connection is still alive, so nothing to do here");
                    try {
                        Thread.sleep(interval * 1000);
                    } catch (InterruptedException e) {
                        getLogger().warn("connection watchdog got interrupted!", e);
                    }
                }
                getLogger().warn("detected connection to be lost!");
                handler.handleLostConnection();
            });
            watchdog.setName(name);
            watchdog.start();
        }
    }
    
}
