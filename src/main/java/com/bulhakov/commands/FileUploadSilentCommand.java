package com.bulhakov.commands;

import com.bulhakov.services.FileService;
import com.bulhakov.services.FormatConvertionService;
import com.bulhakov.util.LocalizationManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class FileUploadSilentCommand extends FileUploadCommand {

    protected FileUploadSilentCommand(LocalizationManager localizationManager, FileService fileService,
                                      FormatConvertionService formatConvertionService) {
        super(localizationManager, fileService, formatConvertionService);
    }

    @Override
    UploadedFileName getFileName(Message message, TelegramLongPollingBot bot) {
        Long telegramUserId = message.getFrom().getId();
        String filename = generateFileWithRandomName(telegramUserId);
        return new UploadedFileName(filename, false);
    }
}
