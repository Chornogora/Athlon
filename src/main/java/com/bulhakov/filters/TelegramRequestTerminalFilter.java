package com.bulhakov.filters;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Consumer;

/**
 * Terminal filter in the chain of responsibility pattern for processing Telegram requests.
 * This filter does not pass the request to any next filter
 * but instead directly handles the request using a provided consumer.
 * It is typically used at the end of a filter chain to perform final processing.
 */
class TelegramRequestTerminalFilter implements TelegramRequestFilter {

    private final Consumer<Update> requestHandler;

    public TelegramRequestTerminalFilter(Consumer<Update> requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void processRequest(Update update) {
        requestHandler.accept(update);
    }
}
