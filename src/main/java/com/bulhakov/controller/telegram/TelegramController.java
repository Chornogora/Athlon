package com.bulhakov.controller.telegram;

import com.bulhakov.commands.Command;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelegramController extends TelegramLongPollingBot {

    private Map<String, Command> commandMap;

    private final String botName;

    public TelegramController(String botToken, String botName) {
        super(botToken);
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String messageText = update.getMessage().getText();
        String commandRepresentation = (messageText == null)
                ? getCaptionOrNull(update.getMessage())
                : getCommandRepresentation(messageText);

        if (commandRepresentation == null) {
            System.out.println("No command found in the message");
            return;
        }

        Command command = commandMap.get(commandRepresentation);
        Optional.of(command).ifPresentOrElse(cmd -> cmd.processUpdate(update, this),
                () -> System.out.println("Command not found: " + commandRepresentation));
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    public void setCommandMap(Map<String, Command> map) {
        this.commandMap = map;
    }

    private String getCaptionOrNull(Message message) {
        if (message.getCaption() == null) {
            return null;
        }
        String caption = message.getCaption();
        return caption == null ? null : getCommandRepresentation(caption);
    }

    String getCommandRepresentation(String messageText) {
        Pattern pattern = Pattern.compile("^(/\\w*)[@ ]?");
        Matcher matcher = pattern.matcher(messageText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}