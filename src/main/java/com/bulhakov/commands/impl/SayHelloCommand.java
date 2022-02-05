package com.bulhakov.commands.impl;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.services.UserService;
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
    public SayHelloCommand(LocalizationManager localizationManager, UserService service) {
        this.localizationManager = localizationManager;
        this.userService = service;
    }

    @Override
    public void processUpdate(Update update, TelegramLongPollingBot controller) {
        Message message = update.getMessage();
        User contact = message.getFrom();

        com.bulhakov.model.User user = userService.findUser(String.valueOf(contact.getId()));
        if(user == null){
            SendMessage sendMessage = getAnswer(message, localizationManager.getStringFromResource("UNKNOWN_USER"));
            execute(controller, sendMessage);
            return;
        }

        String author = contact.getFirstName();
        SendMessage sendMessage = getAnswer(message, localizationManager.getStringFromResource("HELLO") + author);
        execute(controller, sendMessage);
    }
}