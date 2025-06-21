package com.bulhakov.filters;

public abstract class AbstractTelegramRequestFilter implements TelegramRequestFilter {

    protected TelegramRequestFilter nextFilter;

    public void setNextFilter(TelegramRequestFilter nextFilter) {
        this.nextFilter = nextFilter;
    }
}
