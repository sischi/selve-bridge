package com.sischi.selvebridge.gateway.exception;

public class DeviceNotConnectedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DeviceNotConnectedException() {
    }

    public DeviceNotConnectedException(String message) {
        super(message);
    }

    public DeviceNotConnectedException(Throwable cause) {
        super(cause);
    }

    public DeviceNotConnectedException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
