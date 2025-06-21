package com.bulhakov.commands;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {

    void processUpdate(Update update, TelegramLongPollingBot bot);
}