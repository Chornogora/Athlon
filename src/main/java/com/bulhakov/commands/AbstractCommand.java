package com.bulhakov.commands;

import com.bulhakov.util.LocalizationManager;
import com.bulhakov.util.TelegramUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public abstract class AbstractCommand implements Command {

    protected LocalizationManager localizationManager;

    protected AbstractCommand(LocalizationManager localizationManager) {
        this.localizationManager = localizationManager;
    }

    protected SendMessage getAnswer(Message message, String answerText){
        return TelegramUtil.getAnswer(message, answerText);
    }

    protected void execute(TelegramLongPollingBot bot, SendMessage message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to handle command due to exception", e);
        }
    }
}