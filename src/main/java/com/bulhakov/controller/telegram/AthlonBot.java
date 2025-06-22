package com.bulhakov.controller.telegram;

import com.bulhakov.commands.Command;
import com.bulhakov.commands.FileUploadSilentCommand;
import com.bulhakov.filters.TelegramRequestFilterChain;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AthlonBot extends TelegramLongPollingBot {

    private final TelegramRequestFilterChain telegramRequestFilterChain;

    private final String botName;

    private final InlineQueryHandler inlineQueryHandler;

    private final FileUploadSilentCommand fileUploadSilentCommand;

    @Setter
    private Map<String, Command> commandMap;

    public AthlonBot(String botToken, String botName,
                     TelegramRequestFilterChain filterChain,
                     InlineQueryHandler inlineQueryHandler,
                     FileUploadSilentCommand fileUploadSilentCommand) {
        super(botToken);
        this.botName = botName;
        this.telegramRequestFilterChain = filterChain;
        this.inlineQueryHandler = inlineQueryHandler;
        this.fileUploadSilentCommand = fileUploadSilentCommand;
    }

    @Override
    public void onUpdateReceived(Update update) {
        telegramRequestFilterChain.afterFiltering(telegramUpdate -> {
                    try {
                        if (hasTextOrCaption(telegramUpdate)) {
                            log.info("Handling message update");
                            handleMessage(telegramUpdate);
                        } else if (telegramUpdate.hasInlineQuery()) {
                            log.info("Handling inline query update");
                            AnswerInlineQuery answer = inlineQueryHandler.handleInlineQuery(telegramUpdate.getInlineQuery());
                            execute(answer);
                        } else {
                            log.info("Handling silent update");
                            fileUploadSilentCommand.processUpdate(telegramUpdate, this);
                        }
                    } catch (TelegramApiException e) {
                        log.error("Error processing update", e);
                    }
                })
                .processRequest(update);
    }

    private static boolean hasTextOrCaption(Update telegramUpdate) {
        return telegramUpdate.hasMessage() &&
                (StringUtils.isNotEmpty(telegramUpdate.getMessage().getText())
                        || StringUtils.isNotEmpty(telegramUpdate.getMessage().getCaption()));
    }

    private void handleMessage(Update update) {
        String messageText = update.getMessage().getText();
        String commandRepresentation = (messageText == null)
                ? getCaptionOrNull(update.getMessage())
                : getCommandRepresentation(messageText);

        if (commandRepresentation == null) {
            log.info("No command found in the message");
            return;
        }

        Command command = commandMap.get(commandRepresentation);
        Optional.ofNullable(command).ifPresentOrElse(cmd -> cmd.processUpdate(update, this),
                () -> log.info("Command not found: {}", commandRepresentation));
    }

    @Override
    public String getBotUsername() {
        return botName;
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