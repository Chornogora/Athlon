package com.bulhakov.commands;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.services.FileService;
import com.bulhakov.util.LocalizationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.bulhakov.commands.FileUploadNamedCommand.COMMAND_NAME;

@Slf4j
@Component
@CommandMapping(name = COMMAND_NAME)
public class FileUploadNamedCommand extends FileUploadCommand {

    public static final String COMMAND_NAME = "/upload";

    private final FileService fileService;

    @Autowired
    public FileUploadNamedCommand(LocalizationManager localizationManager, FileService fileService) {
        super(localizationManager, fileService);
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
        String generatedId = generateFileName(telegramUserId);
        return new UploadedFileName(generatedId, false);
    }


    private void tryDownloadFile(TelegramLongPollingBot bot, Message message, GetFile getFile) {
        try {
            File file = bot.execute(getFile);
            String filePath = file.getFilePath(); // This is the path on Telegram's server

            // Proceed to download the file using the filePath
            java.io.File downloaded = bot.downloadFile(filePath);
            log.info(downloaded.getAbsolutePath());
        } catch (TelegramApiException e) {
            e.printStackTrace();
            String errorText = localizationManager.getStringFromResource("FILE_UPLOAD_ERROR");
            SendMessage sendMessage = getAnswer(message, errorText);
            execute(bot, sendMessage);
            return;
        }

        String answerText = localizationManager.getStringFromResource("FILE_UPLOADED");
        SendMessage sendMessage = getAnswer(message, answerText);
        execute(bot, sendMessage);
    }
}
