package com.sischi.selvebridge.core.exception;

public class DeviceAlreadyLockedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DeviceAlreadyLockedException() {
    }

    public DeviceAlreadyLockedException(String message) {
        super(message);
    }

    public DeviceAlreadyLockedException(Throwable cause) {
        super(cause);
    }

    public DeviceAlreadyLockedException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
