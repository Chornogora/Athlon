package com.bulhakov.exceptions;

public class WrongDateException extends Exception {

    private final CAUSE dateCause;

    public enum CAUSE{
        LATE, FUTURE
    }

    public WrongDateException(CAUSE cause){
        this.dateCause = cause;
    }

    public CAUSE getDateExceptionCause(){
        return this.dateCause;
    }
}
