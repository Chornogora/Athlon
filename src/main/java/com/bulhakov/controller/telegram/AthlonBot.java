package com.bulhakov.controller.telegram;

import com.bulhakov.commands.Command;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AthlonBot extends TelegramLongPollingBot {

    private Map<String, Command> commandMap;

    private final String botName;

    private final InlineQueryHandler inlineQueryHandler;

    public AthlonBot(String botToken, String botName, InlineQueryHandler inlineQueryHandler) {
        super(botToken);
        this.botName = botName;
        this.inlineQueryHandler = inlineQueryHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                System.out.println("Handling message update");
                handleMessage(update);
            } else if (update.hasInlineQuery()) {
                System.out.println("Handling inline query update");
                AnswerInlineQuery answer = inlineQueryHandler.handleInlineQuery(update.getInlineQuery());
                execute(answer);
            }
        } catch (TelegramApiException e) {
            System.err.println("Error processing update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleMessage(Update update) {
        String messageText = update.getMessage().getText();
        String commandRepresentation = (messageText == null)
                ? getCaptionOrNull(update.getMessage())
                : getCommandRepresentation(messageText);

        if (commandRepresentation == null) {
            System.out.println("No command found in the message");
            return;
        }

        Command command = commandMap.get(commandRepresentation);
        Optional.ofNullable(command).ifPresentOrElse(cmd -> cmd.processUpdate(update, this),
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