package com.bulhakov.commands;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.services.FileService;
import com.bulhakov.util.LocalizationManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;
import java.util.UUID;

import static com.bulhakov.commands.FileUploadCommand.COMMAND_NAME;

@Slf4j
@Component
@CommandMapping(name = COMMAND_NAME)
public class FileUploadCommand extends AbstractCommand {

    public static final String COMMAND_NAME = "/upload";

    private final FileService fileService;

    @Autowired
    public FileUploadCommand(LocalizationManager localizationManager, FileService fileService) {
        super(localizationManager);
        this.fileService = fileService;
    }

    @Override
    public void processUpdate(Update update, TelegramLongPollingBot bot) {
        Message message = update.getMessage();

        Optional<String> fileIdOptional = tryExtractingFileId(message);
        fileIdOptional.ifPresentOrElse(fileId -> {

            Long telegramUserId = message.getFrom().getId();
            Pair<String, Boolean> filenameGenerationResult;

            try {
                filenameGenerationResult = getFileName(message, telegramUserId);
            } catch (IllegalArgumentException e) {
                SendMessage sendMessage = getAnswer(message, e.getMessage());
                execute(bot, sendMessage);
                return;
            }
            fileService.storeFile(telegramUserId, filenameGenerationResult.getLeft(), fileId);

            String successText = getFileUploadedText(filenameGenerationResult.getLeft(), filenameGenerationResult.getRight());
            SendMessage sendMessage = getAnswer(message, successText);
            execute(bot, sendMessage);

            //GetFile getFile = new GetFile();
            //getFile.setFileId(fileId);
            //tryDownloadFile(bot, message, getFile);
        }, () -> {
            String errorText = localizationManager.getStringFromResource("FILE_UPLOAD_ERROR");
            SendMessage sendMessage = getAnswer(message, errorText);
            execute(bot, sendMessage);
        });
    }

    private Pair<String, Boolean> getFileName(Message message, Long telegramUserId) {
        String messageContent = message.getCaption().substring(COMMAND_NAME.length()).stripLeading();
        if (!messageContent.isEmpty()) {
            if (fileService.getFileForUser(telegramUserId, messageContent).isPresent()) {
                throw new IllegalArgumentException(localizationManager.getStringFromResource("FILE_NAME_EXISTS"));
            }
            return Pair.of(messageContent, true);
        }

        // Generate a unique file username based on a random UUID
        String generatedId = null;
        while (generatedId == null) {
            String randomId = UUID.randomUUID().toString();
            String randomIdShort = randomId.substring(0, randomId.indexOf("-"));
            if (fileService.getFileForUser(telegramUserId, randomIdShort).isEmpty()) {
                generatedId = randomIdShort;
            }
        }
        return Pair.of(generatedId, false);
    }

    private String getFileUploadedText(String fileName, boolean isCustomName) {
        String basicMessage = localizationManager.getStringFromResource("FILE_UPLOAD_SUCCESS");
        return (isCustomName)
                ? "%s. File username: %s".formatted(basicMessage, fileName)
                : "%s. File username: %s. You can change it using the /rename command".formatted(basicMessage, fileName);
    }

    private Optional<String> tryExtractingFileId(Message message) {
        //TODO support different file types
        if (message.hasAudio()) {
            return Optional.ofNullable(message.getAudio().getFileId());
        } else if (message.hasDocument()) {
            return Optional.ofNullable(message.getDocument().getFileId());
        } else if (message.hasVoice()) {
            return Optional.ofNullable(message.getVoice().getFileId());
        } else {
            return Optional.empty();
        }
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
