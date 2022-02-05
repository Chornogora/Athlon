package com.bulhakov.controller.telegram;

import com.bulhakov.commands.Command;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelegramController extends TelegramLongPollingBot {

    private Map<String, Command> commandMap;

    private String botName;

    private String botToken;

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
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
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

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
}