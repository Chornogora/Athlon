package com.bulhakov.filters;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramRequestFilter {

    void processRequest(Update update);
}
