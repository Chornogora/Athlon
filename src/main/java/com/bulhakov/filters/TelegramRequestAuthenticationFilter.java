package com.bulhakov.filters;

import com.bulhakov.model.User;
import com.bulhakov.services.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramRequestAuthenticationFilter extends AbstractTelegramRequestFilter {

    private final UserService userService;

    public TelegramRequestAuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void processRequest(Update update) {
        org.telegram.telegrambots.meta.api.objects.User telegramUser = update.getMessage().getFrom();
        Long telegramUserId = telegramUser.getId();
        User user = userService.findUserByExternalId(telegramUserId);
        if (user == null) {
            System.out.println("User not found for ID: " + telegramUserId);
            saveNewUser(telegramUser);
            return;
        } else if (!telegramUser.getUserName().equals(user.getUsername())) {
            updateUsername(telegramUser, user);
        }

        if (nextFilter != null) {
            nextFilter.processRequest(update);
        }
    }

    private void updateUsername(org.telegram.telegrambots.meta.api.objects.User telegramUser, User user) {
        User updatedUser = new User(user.getId(), user.getExternalId(), user.getLogin(), telegramUser.getUserName(), null, user.getBanned());
        userService.updateUser(updatedUser);
        System.out.println("Updated username for user: " + user.getId());
    }

    private void saveNewUser(org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        User newUser = new User(null, telegramUser.getId(), null, telegramUser.getUserName(), null, false);
        userService.addUser(newUser);
        System.out.println("New user saved");
    }
}
