package com.bulhakov.configuration;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.commands.Command;
import com.bulhakov.commands.FileUploadSilentCommand;
import com.bulhakov.controller.telegram.AthlonBot;
import com.bulhakov.controller.telegram.InlineQueryHandler;
import com.bulhakov.filters.TelegramRequestFilterChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class TelegramControllerConfiguration {

    @Value("${telegram-bot.bot-name}")
    private String botName;

    @Value("${telegram-bot.bot-token}")
    private String botToken;

    private AthlonBot bot;

    @Bean
    @Qualifier("commandsByTelegramBotCommand")
    public Map<String, Command> commandsMap(List<Command> commands) {
        Map<String, Command> commandsMap = new HashMap<>();
        for (Command command : commands) {
            if (command.getClass().isAnnotationPresent(CommandMapping.class)) {
                commandsMap.put(getCommandStringRepresentation(command), command);
            }
        }
        return commandsMap;
    }

    @Bean
    public AthlonBot telegramController(InlineQueryHandler inlineQueryHandler,
                                        TelegramRequestFilterChain filterChain,
                                        @Qualifier("commandsByTelegramBotCommand")
                                        Map<String, Command> commandsMap,
                                        FileUploadSilentCommand fileUploadSilentCommand) {
        AthlonBot bot = new AthlonBot(botToken, botName, filterChain, inlineQueryHandler, fileUploadSilentCommand);
        bot.setCommandMap(commandsMap);
        this.bot = bot;
        return bot;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerTelegramBot() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to register Telegram bot", e);
        }
    }

    private String getCommandStringRepresentation(Command command) {
        Class<? extends Command> cls = command.getClass();
        CommandMapping annotation = cls.getAnnotation(CommandMapping.class);
        return annotation.name();
    }

}