package com.bulhakov.configuration;

import com.bulhakov.controller.telegram.AthlonBot;
import com.bulhakov.filters.TelegramRequestAuthenticationFilter;
import com.bulhakov.filters.TelegramRequestAuthorizationFilter;
import com.bulhakov.filters.TelegramRequestFilterChain;
import com.bulhakov.services.UserService;
import com.bulhakov.util.LocalizationManager;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class TelegramRequestFilterConfiguration {

    private TelegramRequestFilterChain telegramRequestFilterChain;
    private TelegramRequestAuthenticationFilter authenticationFilter;
    private TelegramRequestAuthorizationFilter authorizationFilter;

    @Bean
    public TelegramRequestFilterChain emptyChain() {
        this.telegramRequestFilterChain = new TelegramRequestFilterChain();
        return telegramRequestFilterChain;
    }

    @Bean
    public TelegramRequestAuthorizationFilter telegramRequestAuthorizationFilter(AthlonBot bot,
                                                                                 UserService userService,
                                                                                 LocalizationManager localizationManager) {
        this.authorizationFilter = new TelegramRequestAuthorizationFilter(userService, bot, localizationManager);
        return authorizationFilter;
    }

    @Bean
    public TelegramRequestAuthenticationFilter telegramRequestAuthenticationFilter(UserService userService) {
        this.authenticationFilter = new TelegramRequestAuthenticationFilter(userService);
        return authenticationFilter;
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @EventListener(ApplicationReadyEvent.class)
    public void setupFilterChain() {
        telegramRequestFilterChain.addFilter(authenticationFilter)
                .addFilter(authorizationFilter);
    }
}
