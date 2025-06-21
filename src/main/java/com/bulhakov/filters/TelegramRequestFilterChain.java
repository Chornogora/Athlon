package com.bulhakov.filters;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.LinkedList;
import java.util.function.Consumer;

public class TelegramRequestFilterChain implements TelegramRequestFilter {

    private final LinkedList<TelegramRequestFilter> filters = new LinkedList<>();

    public TelegramRequestFilterChain addFilter(TelegramRequestFilter filter) {
        if (!filters.isEmpty() && filters.getLast() instanceof AbstractTelegramRequestFilter atrf) {
            atrf.setNextFilter(filter);
        } else if (!filters.isEmpty()) {
            throw new IllegalStateException("Unable to add filter: last added filter doesn't support chaining");
        }
        filters.add(filter);
        return this;
    }

    public TelegramRequestFilterChain afterFiltering(Consumer<Update> updateConsumer) {
        if (filters.getLast() instanceof TelegramRequestTerminalFilter) {
            filters.removeLast();
        }

        var terminalFilter = new TelegramRequestTerminalFilter(updateConsumer);
        if (!filters.isEmpty() && filters.getLast() instanceof AbstractTelegramRequestFilter atrf) {
            atrf.setNextFilter(terminalFilter);
        }
        filters.addLast(terminalFilter);
        return this;
    }

    @Override
    public void processRequest(Update update) {
        if (filters.isEmpty()) {
            throw new IllegalStateException("No filters in the chain");
        }
        filters.getFirst().processRequest(update);
    }
}
