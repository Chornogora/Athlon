package com.bulhakov.commands;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.util.LocalizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
@CommandMapping(name = "/hello")
public class SayHelloCommand extends AbstractCommand {

    @Autowired
    public SayHelloCommand(LocalizationManager localizationManager) {
        super(localizationManager);
    }

    @Override
    public void processUpdate(Update update, TelegramLongPollingBot bot) {
        Message message = update.getMessage();
        User contact = message.getFrom();
        String answerText = localizationManager.getStringFromResource("HELLO") + ", @" + contact.getUserName() + "!";
        SendMessage sendMessage = getAnswer(message, answerText);
        execute(bot, sendMessage);
    }
}