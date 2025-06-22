package com.bulhakov.commands;

import com.bulhakov.services.FileService;
import com.bulhakov.util.LocalizationManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class FileUploadSilentCommand extends FileUploadCommand {

    protected FileUploadSilentCommand(LocalizationManager localizationManager,
                                      FileService fileService) {
        super(localizationManager, fileService);
    }

    @Override
    UploadedFileName getFileName(Message message, TelegramLongPollingBot bot) {
        Long telegramUserId = message.getFrom().getId();
        String filename = generateFileName(telegramUserId);
        return new UploadedFileName(filename, false);
    }
}
