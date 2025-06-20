package com.bulhakov.controller.telegram.configuration;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.commands.Command;
import com.bulhakov.commands.FileRenameCommand;
import com.bulhakov.commands.FileUploadCommand;
import com.bulhakov.commands.RegisterCommand;
import com.bulhakov.commands.SayHelloCommand;
import com.bulhakov.commands.SetBirthdayCommand;
import com.bulhakov.commands.SetLocaleCommand;
import com.bulhakov.controller.telegram.InlineQueryHandler;
import com.bulhakov.controller.telegram.TelegramController;
import com.bulhakov.util.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TelegramControllerConfiguration {

    private final SayHelloCommand helloCommand;

    private final SetLocaleCommand localeCommand;

    private final RegisterCommand registerCommand;

    private final SetBirthdayCommand birthdayCommand;

    private final FileUploadCommand fileUploadCommand;

    private final FileRenameCommand fileRenameCommand;

    private final ApplicationProperties applicationProperties;

    @Autowired
    public TelegramControllerConfiguration(SayHelloCommand command,
                                           SetLocaleCommand localeCommand,
                                           RegisterCommand registerCommand,
                                           SetBirthdayCommand birthdayCommand,
                                           FileUploadCommand fileUploadCommand,
                                           FileRenameCommand fileRenameCommand,
                                           ApplicationProperties applicationProperties) {
        this.helloCommand = command;
        this.localeCommand = localeCommand;
        this.registerCommand = registerCommand;
        this.birthdayCommand = birthdayCommand;
        this.fileUploadCommand = fileUploadCommand;
        this.fileRenameCommand = fileRenameCommand;
        this.applicationProperties = applicationProperties;
    }

    @Autowired
    private void initBot(InlineQueryHandler inlineQueryHandler) {
        TelegramController controller = new TelegramController(
                applicationProperties.getProperty("botToken"),
                applicationProperties.getProperty("botName"),
                inlineQueryHandler);
        controller.setCommandMap(commandMap());

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(controller);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Command> commandMap() {
        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put(getCommandStringRepresentation(helloCommand), helloCommand);
        commandMap.put(getCommandStringRepresentation(localeCommand), localeCommand);
        commandMap.put(getCommandStringRepresentation(registerCommand), registerCommand);
        commandMap.put(getCommandStringRepresentation(birthdayCommand), birthdayCommand);
        commandMap.put(getCommandStringRepresentation(fileUploadCommand), fileUploadCommand);
        commandMap.put(getCommandStringRepresentation(fileRenameCommand), fileRenameCommand);
        return commandMap;
    }

    private String getCommandStringRepresentation(Command command) {
        Class<? extends Command> cls = command.getClass();
        CommandMapping annotation = cls.getAnnotation(CommandMapping.class);
        return annotation.name();
    }
}