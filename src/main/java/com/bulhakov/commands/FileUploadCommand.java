package com.bulhakov.commands;

import com.bulhakov.exceptions.MediaInputException;
import com.bulhakov.services.FileService;
import com.bulhakov.util.LocalizationManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

abstract class FileUploadCommand extends AbstractCommand {

    protected final FileService fileService;

    protected FileUploadCommand(LocalizationManager localizationManager, FileService fileService) {
        super(localizationManager);
        this.fileService = fileService;
    }

    @Override
    public void processUpdate(Update update, TelegramLongPollingBot bot) {
        Message message = update.getMessage();
        if (!ofCorrectType(message)) {
            String errorText = localizationManager.getStringFromResource("FILE_UPLOAD_ERROR");
            SendMessage sendMessage = getAnswer(update.getMessage(), errorText);
            execute(bot, sendMessage);
            return;
        }

        UploadedFileName uploadedFileName = getFileName(message, bot);
        try {
            if (message.hasVoice()) {
                saveVoice(message, uploadedFileName.fileName);
            } else if (message.hasAudio()) {
                trySaveVoiceTrackFromAudio(message, bot, uploadedFileName.fileName);
            } else if (message.hasVideo()) {
                trySaveVoiceTrackFromVideo(message, bot, uploadedFileName.fileName);
            }
        } catch (MediaInputException e) {
            String errorText = localizationManager.getStringFromResource("FILE_UPLOAD_ERROR");
            SendMessage sendMessage = getAnswer(message, errorText);
            execute(bot, sendMessage);
            return;
        }

        String successText = getFileUploadedText(uploadedFileName.fileName(), uploadedFileName.isCustomName());
        SendMessage sendMessage = getAnswer(message, successText);
        execute(bot, sendMessage);
    }

    private boolean ofCorrectType(Message message) {
        return message != null && (message.hasVoice()
                || message.hasAudio()
                || message.hasVideo());
    }

    private void saveVoice(Message message, String filename) {
        String fileId = message.getVoice().getFileId();
        Long telegramUserId = message.getFrom().getId();
        fileService.storeFile(telegramUserId, filename, fileId);
    }

    private void trySaveVoiceTrackFromAudio(Message message, TelegramLongPollingBot bot, String filename) {
        String fileId = message.getAudio().getFileId();
        Long telegramUserId = message.getFrom().getId();
    }

    private void trySaveVoiceTrackFromVideo(Message message, TelegramLongPollingBot bot, String filename) {
        String fileId = message.getVideo().getFileId();
        Long telegramUserId = message.getFrom().getId();
    }

    protected String generateFileName(Long telegramUserId) {
        String generatedId = null;
        while (generatedId == null) {
            String randomId = UUID.randomUUID().toString();
            String randomIdShort = randomId.substring(0, randomId.indexOf("-"));
            if (fileService.getFileForUser(telegramUserId, randomIdShort).isEmpty()) {
                generatedId = randomIdShort;
            }
        }
        return generatedId;
    }

    abstract UploadedFileName getFileName(Message message, TelegramLongPollingBot bot);

    private String getFileUploadedText(String fileName, boolean isCustomName) {
        String basicMessage = localizationManager.getStringFromResource("FILE_UPLOAD_SUCCESS");
        return (isCustomName)
                ? "%s. File username: %s".formatted(basicMessage, fileName)
                : "%s. File username: %s. You can change it using the /rename command".formatted(basicMessage, fileName);
    }

    protected record UploadedFileName(String fileName, boolean isCustomName){}
}
