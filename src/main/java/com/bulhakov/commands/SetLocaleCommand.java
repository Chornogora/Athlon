package com.bulhakov.commands;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.util.LocalizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@CommandMapping(name = "/locale")
public class SetLocaleCommand extends AbstractCommand {

    @Autowired
    public SetLocaleCommand(LocalizationManager localizationManager) {
        super(localizationManager);
    }

    @Override
    public void processUpdate(Update update, TelegramLongPollingBot controller) {
        Message message = update.getMessage();
        String[] words = message.getText().split(" ");

        SendMessage sendMessage;
        if (words.length == 1) {
            sendMessage = getAnswer(message, localizationManager.getStringFromResource("LOCALES"));
        } else {
            try {
                localizationManager.setResource(words[1]);
                sendMessage = getAnswer(message, localizationManager.getStringFromResource("SUCCESS_LOCALIZATION"));
            } catch (IllegalArgumentException e) {
                sendMessage = getAnswer(message, localizationManager.getStringFromResource("FAILED_LOCALIZATION"));
            }
        }
        execute(controller, sendMessage);
    }
}
