package com.bulhakov.filters;

import com.bulhakov.model.User;
import com.bulhakov.services.UserService;
import com.bulhakov.util.LocalizationManager;
import com.bulhakov.util.TelegramUtil;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramRequestAuthorizationFilter extends AbstractTelegramRequestFilter {

    private final TelegramLongPollingBot bot;

    private final UserService userService;
    private final LocalizationManager localizationManager;

    public TelegramRequestAuthorizationFilter(UserService userService, TelegramLongPollingBot bot,
                                              LocalizationManager localizationManager) {
        this.bot = bot;
        this.userService = userService;
        this.localizationManager = localizationManager;
    }

    @Override
    public void processRequest(Update update) {
        org.telegram.telegrambots.meta.api.objects.User telegramUser = update.getMessage().getFrom();
        Long telegramUserId = telegramUser.getId();
        User user = userService.findUserByExternalId(telegramUserId);

        if (user.getBanned()) {
            try {
                String bannedMessage = localizationManager.getStringFromResource("USER_BANNED");
                SendMessage sendMessage = TelegramUtil.getAnswer(update.getMessage(), bannedMessage);
                bot.execute(sendMessage);
                return;
            } catch (TelegramApiException e) {
                e.printStackTrace();
                return;
            }
        }

        if (nextFilter != null) {
            nextFilter.processRequest(update);
        }
    }
}
