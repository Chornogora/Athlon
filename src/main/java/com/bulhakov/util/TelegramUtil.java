package com.bulhakov.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class TelegramUtil {

    public static SendMessage getAnswer(Message message, String answerText) {
        SendMessage s = new SendMessage();
        s.enableHtml(true);
        s.setChatId(message.getChatId().toString());
        s.setReplyToMessageId(message.getMessageId());
        s.setText(answerText);
        return s;
    }

    public static User getUser(Update update) {
        return update.getMessage() == null
                ? update.getInlineQuery().getFrom()
                : update.getMessage().getFrom();
    }
}
