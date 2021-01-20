package com.sischi.selvebridge.core.exception;

public class ConversationCancelled extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConversationCancelled() {
    }

    public ConversationCancelled(String message) {
        super(message);
    }

    public ConversationCancelled(Throwable cause) {
        super(cause);
    }

    public ConversationCancelled(String message, Throwable cause) {
        super(message, cause);
    }
    
}
