package com.sischi.selvebridge.util;

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
            getLogger().warn("connection watchdog '{}' is disabled due to corresponding watchdog interval is set to '{}'!", name, interval);
            return;
        }

        if(handler == null) {
            getLogger().error("no connection watchdog handler found for watchdog '{}'!", name);
            return;
        }

        if (watchdog == null || !watchdog.isAlive()) {
            watchdog = new Thread(() -> {
                getLogger().info("connection watchdog '{}' started", name);
                while (handler.checkConnection()) {
                    getLogger().debug("'{}': the connection is still alive, so nothing to do here", name);
                    try {
                        Thread.sleep(interval * 1000);
                    } catch (InterruptedException e) {
                        getLogger().warn("connection watchdog '{}' got interrupted!", name, e);
                    }
                }
                getLogger().warn("'{}': detected connection to be lost!", name);
                handler.handleLostConnection();
            });
            watchdog.setName(name);
            watchdog.start();
        }
    }
    
}
