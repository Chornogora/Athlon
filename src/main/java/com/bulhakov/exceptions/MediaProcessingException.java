package com.bulhakov.exceptions;

public class MediaProcessingException extends RuntimeException {

    public MediaProcessingException(String s) {
        super();
    }

    public MediaProcessingException(String message, Throwable e) {
        super(message, e);
    }
}
