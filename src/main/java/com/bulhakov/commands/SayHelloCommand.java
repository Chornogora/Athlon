package com.bulhakov.commands;

import com.bulhakov.annotations.CommandMapping;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@CommandMapping(name = "/hello")
public class SayHelloCommand implements Command {

    @Override
    public void processUpdate(Update update, TelegramLongPollingBot controller) {
        Message message = update.getMessage();
        User contact = message.getFrom();
        String author = contact.getFirstName();

        SendMessage s = new SendMessage();
        s.enableMarkdown(true);
        s.setChatId(message.getChatId().toString());
        s.setReplyToMessageId(message.getMessageId());

        s.setText("Hello, " + author);

        try {
            controller.execute(s);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
}