package com.sischi.selvebridge.core;

import java.util.concurrent.Semaphore;

import com.sischi.selvebridge.core.util.HasLogger;

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
            getLogger().warn("reconnect thread is disabled due to corresponding reconnect interval property is set to '"+ interval +"'!");
            return;
        }

        if(handler == null) {
            getLogger().error("no reconnect thread handler found!");
            return;
        }

        if (mutex.tryAcquire()) {
            if (thread == null || !thread.isAlive()) {
                thread = new Thread(() -> {
                    getLogger().debug("reconnect thread started ...");
                    while (!handler.checkConnection()) {
                        getLogger().info("still not connected, so will try again soon ...");
                        handler.disconnect();
                        try {
                            getLogger().debug("waiting for "+ interval +" sec ...");
                            Thread.sleep(interval * 1000);
                        } catch (InterruptedException e) {
                            getLogger().warn("reconnect thread got interrupted!", e);
                        }
                        handler.connect();
                    }
                    getLogger().debug("connection successfully established! finishing reconnect thread!");
                    handler.handleSuccessfulReconnect();
                });
                thread.setName(name);
                thread.start();
            } else {
                getLogger().info("a reconnect thread is already running!");
            }
            mutex.release();
        } else {
            getLogger().info("could not acquire reconnect mutex");
        }
    }
}
