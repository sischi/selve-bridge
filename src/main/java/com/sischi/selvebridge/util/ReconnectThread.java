package com.sischi.selvebridge.util;

import java.util.concurrent.Semaphore;

public class ReconnectThread implements HasLogger {
    
    private String name;
    private Thread thread = null;
    private int interval;

    private Semaphore mutex = new Semaphore(1);

    private ReconnectThreadHandler handler;

    public interface ReconnectThreadHandler {
        void connect();
        void disconnect();
        boolean checkConnection();
        void handleSuccessfulReconnect();
    }

    public ReconnectThread(String name, int interval, ReconnectThreadHandler handler) {
        this.name = name;
        this.interval = interval;
        this.handler = handler;
    }

    public void start() {
        if (interval <= 0) {
            getLogger().warn("reconnect thread '{}' is disabled due to corresponding reconnect interval property is set to '{}'!", name, interval);
            return;
        }

        if(handler == null) {
            getLogger().error("no reconnect thread handler found reconnect thread '{}'!", name);
            return;
        }

        if (mutex.tryAcquire()) {
            if (thread == null || !thread.isAlive()) {
                thread = new Thread(() -> {
                    getLogger().info("reconnect thread '{}' started", name);
                    while (!handler.checkConnection()) {
                        getLogger().info("still not connected, so will try again in '{}' seconds", interval);
                        handler.disconnect();
                        try {
                            getLogger().debug("waiting for '{}' seconds ...", interval);
                            Thread.sleep(interval * 1000);
                        } catch (InterruptedException e) {
                            getLogger().warn("reconnect thread '{}' got interrupted!", name, e);
                        }
                        handler.connect();
                    }
                    getLogger().info("'{}': connection successfully established! finishing reconnect thread!", name);
                    handler.handleSuccessfulReconnect();
                });
                thread.setName(name);
                thread.start();
            } else {
                getLogger().info("a reconnect thread '{}' is already running!", name);
            }
            mutex.release();
        } else {
            getLogger().info("could not acquire reconnect mutex for '{}'", name);
        }
    }
}
