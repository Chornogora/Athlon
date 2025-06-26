package com.bulhakov.commands;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.services.FileService;
import com.bulhakov.services.FormatConvertionService;
import com.bulhakov.util.LocalizationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.bulhakov.commands.FileUploadNamedCommand.COMMAND_NAME;

@Slf4j
@Component
@CommandMapping(name = COMMAND_NAME)
public class FileUploadNamedCommand extends FileUploadCommand {

    public static final String COMMAND_NAME = "/upload";

    private final FileService fileService;

    @Autowired
    public FileUploadNamedCommand(LocalizationManager localizationManager, FileService fileService,
                                  FormatConvertionService formatConvertionService) {
        super(localizationManager, fileService, formatConvertionService);
        this.fileService = fileService;
    }

    UploadedFileName getFileName(Message message, TelegramLongPollingBot bot) {
        Long telegramUserId = message.getFrom().getId();
        String messageContent = message.getCaption().substring(COMMAND_NAME.length()).stripLeading();
        if (!messageContent.isEmpty()) {
            if (fileService.getFileForUser(telegramUserId, messageContent).isPresent()) {
                throw new IllegalArgumentException(localizationManager.getStringFromResource("FILE_NAME_EXISTS"));
            }
            return new UploadedFileName(messageContent, true);
        }

        // Generate a unique file username based on a random UUID
        String generatedId = generateFileWithRandomName(telegramUserId);
        return new UploadedFileName(generatedId, false);
    }
}
