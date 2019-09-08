package com.bulhakov.controller.telegram;

import com.bulhakov.commands.Command;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelegramController extends TelegramLongPollingBot {

    private Map<String, Command> commandMap;

    @Override
    public void onUpdateReceived(Update update) {
        String messageText = update.getMessage().getText();
        if(messageText == null) {
            return;
        }
        String commandRepresentation = getCommandRepresentation(messageText);
        if(commandRepresentation == null){
            return;
        }
        Command command = commandMap.get(commandRepresentation);
        if(command != null){
            command.processUpdate(update, this);
        }
    }

    @Override
    public String getBotUsername() {
        return "Athlon";
    }

    @Override
    public String getBotToken() {
        return "903971949:AAFF61XZzAJE4SYFIq7PnHY1WNR1KSy_EnY";
    }

    public void setCommandMap(Map<String, Command> map){
        this.commandMap = map;
    }

    String getCommandRepresentation(String messageText){
        Pattern pattern = Pattern.compile("^(/\\w*)[@ ]?");
        Matcher matcher = pattern.matcher(messageText);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }
}