package com.bulhakov.commands;

import com.bulhakov.services.UserService;
import com.bulhakov.util.LocalizationManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class AbstractCommand implements Command {

    UserService userService;

    protected LocalizationManager localizationManager;

    protected AbstractCommand(LocalizationManager localizationManager) {
        this.localizationManager = localizationManager;
    }

    SendMessage getAnswer(Message message, String answerText){
        SendMessage s = new SendMessage();
        s.enableHtml(true);
        s.setChatId(message.getChatId().toString());
        s.setReplyToMessageId(message.getMessageId());
        s.setText(answerText);
        return s;
    }

    void execute(TelegramLongPollingBot controller, SendMessage message){
        try {
            controller.execute(message);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
}