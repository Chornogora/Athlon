package com.bulhakov.commands;

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
@CommandMapping(name = "/register")
public class RegisterCommand extends AbstractCommand {

    @Autowired
    private RegisterCommand(LocalizationManager localizationManager, UserService userService){
        this.localizationManager = localizationManager;
        this.userService = userService;
    }

    @Override
    public void processUpdate(Update update, TelegramLongPollingBot controller) {
        SendMessage sendMessage;

        Message message = update.getMessage();
        User user = message.getFrom();
        Long userId = user.getId();
        if(user.getUserName() == null){
            sendMessage = getAnswer(message,
                    localizationManager.getStringFromResource("NULL_NAME"));
            execute(controller, sendMessage);
            return;
        }

        com.bulhakov.model.User dbUser = userService.findUser(String.valueOf(userId));

        if(dbUser == null){
            dbUser = new com.bulhakov.model.User(String.valueOf(userId), user.getUserName(), user.getFirstName());
            userService.addUser(dbUser);
            sendMessage = getAnswer(message,
                    localizationManager.getStringFromResource("NEW_USER") + dbUser.getName());
        }else{
            sendMessage = getAnswer(message,
                    localizationManager.getStringFromResource("ALREADY_KNOW") + dbUser.getName());
        }
        execute(controller, sendMessage);
    }
}
