package com.bulhakov.controller.telegram.configuration;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.commands.Command;
import com.bulhakov.commands.SayHelloCommand;
import com.bulhakov.controller.telegram.TelegramController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TelegramControllerConfiguration {

    @Autowired
    private void initBot(){
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        TelegramController controller = new TelegramController();
        try{
            telegramBotsApi.registerBot(controller);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
        controller.setCommandMap(commandMap());
    }

    public Map<String, Command> commandMap(){
        Map<String, Command> commandMap =  new HashMap<>();
        Command helloCommand = new SayHelloCommand();
        commandMap.put(getCommandStringRepresentation(helloCommand), helloCommand);
        return commandMap;
    }

    private String getCommandStringRepresentation(Command command){
        Class<? extends Command> cls = command.getClass();
        CommandMapping annotation = cls.getAnnotation(CommandMapping.class);
        return annotation.name();
    }
}
