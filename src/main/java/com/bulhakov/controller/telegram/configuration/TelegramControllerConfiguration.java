package com.bulhakov.controller.telegram.configuration;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.commands.Command;
import com.bulhakov.commands.impl.FileUploadCommand;
import com.bulhakov.commands.impl.RegisterCommand;
import com.bulhakov.commands.impl.SayHelloCommand;
import com.bulhakov.commands.impl.SetBirthdayCommand;
import com.bulhakov.commands.impl.SetLocaleCommand;
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

    private final ApplicationProperties applicationProperties;

    @Autowired
    public TelegramControllerConfiguration(SayHelloCommand command,
                                           SetLocaleCommand localeCommand,
                                           RegisterCommand registerCommand,
                                           SetBirthdayCommand birthdayCommand,
                                           FileUploadCommand fileUploadCommand,
                                           ApplicationProperties applicationProperties) {
        this.helloCommand = command;
        this.localeCommand = localeCommand;
        this.registerCommand = registerCommand;
        this.birthdayCommand = birthdayCommand;
        this.fileUploadCommand = fileUploadCommand;
        this.applicationProperties = applicationProperties;
    }

    @Autowired
    private void initBot(){
        TelegramController controller = new TelegramController(
                applicationProperties.getProperty("botToken"),
                applicationProperties.getProperty("botName"));
        controller.setCommandMap(commandMap());

        try{
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(controller);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }

    public Map<String, Command> commandMap(){
        Map<String, Command> commandMap =  new HashMap<>();
        commandMap.put(getCommandStringRepresentation(helloCommand), helloCommand);
        commandMap.put(getCommandStringRepresentation(localeCommand), localeCommand);
        commandMap.put(getCommandStringRepresentation(registerCommand), registerCommand);
        commandMap.put(getCommandStringRepresentation(birthdayCommand), birthdayCommand);
        commandMap.put(getCommandStringRepresentation(fileUploadCommand), fileUploadCommand);
        return commandMap;
    }

    private String getCommandStringRepresentation(Command command){
        Class<? extends Command> cls = command.getClass();
        CommandMapping annotation = cls.getAnnotation(CommandMapping.class);
        return annotation.name();
    }
}