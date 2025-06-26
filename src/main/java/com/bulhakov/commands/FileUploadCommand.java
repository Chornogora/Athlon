package com.bulhakov.commands;

import com.bulhakov.exceptions.MediaProcessingException;
import com.bulhakov.services.FileService;
import com.bulhakov.services.FormatConvertionService;
import com.bulhakov.util.LocalizationManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.UUID;

@Slf4j
@Component
abstract class FileUploadCommand extends AbstractCommand {

    private static final String OGG_EXTENSION = ".ogg";

    @Value("${athlon-setup.temporary-file-download-directory}")
    public String temporaryFileDownloadDirectory;

    @Value("${athlon-setup.temporary-file-convertion-directory}")
    public String temporaryFileConvertionDirectory;

    protected final FormatConvertionService fileConvertionService;
    protected final FileService fileService;

    protected FileUploadCommand(LocalizationManager localizationManager, FileService fileService,
                                FormatConvertionService fileConvertionService) {
        super(localizationManager);
        this.fileService = fileService;
        this.fileConvertionService = fileConvertionService;
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
            } else if (message.hasAudio() || message.hasVideo()) {
                trySaveVoiceTrackFromAudioOrVideo(message, bot, uploadedFileName.fileName);
            }
        } catch (MediaProcessingException e) {
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

    private void trySaveVoiceTrackFromAudioOrVideo(Message message, TelegramLongPollingBot bot, String filename) {
        String inputFileId = message.hasAudio() ? message.getAudio().getFileId() : message.getVideo().getFileId();
        Long telegramUserId = message.getFrom().getId();

        File outputFile = generateFileWithRandomName(temporaryFileDownloadDirectory, StringUtils.EMPTY);
        File oggFile = null;
        try {
            File downloaded = tryDownloadFile(bot, inputFileId, outputFile);
            oggFile = generateFileWithRandomName(temporaryFileConvertionDirectory, OGG_EXTENSION);
            boolean converted = fileConvertionService.convertMp3ToOgg(downloaded, oggFile);
            if (converted) {
                log.info("File {} converted to ogg", inputFileId);
                String voiceFileId = uploadVoiceFile(oggFile, message, bot);
                fileService.storeFile(telegramUserId, filename, voiceFileId);
            }
        } catch (TelegramApiException e) {
            log.error("Failed to upload voice file", e);
            throw new MediaProcessingException("Failed to upload voice file", e);
        } finally {
            tryDeleteFile(outputFile);
            tryDeleteFile(oggFile);
        }

    }

    private File tryDownloadFile(TelegramLongPollingBot bot, String fileId, File outputFile) {
        try {
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);
            org.telegram.telegrambots.meta.api.objects.File telegramFile = bot.execute(getFile);
            String filePath = telegramFile.getFilePath(); // This is the path on Telegram's server

            File downloaded = bot.downloadFile(filePath, outputFile);
            log.info("File {} downloaded to {}", fileId, downloaded.getAbsolutePath());
            return downloaded;
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to download file from Telegram", e);
        }
    }

    private String uploadVoiceFile(File oggFile, Message message, TelegramLongPollingBot bot) throws TelegramApiException {
        SendVoice sendVoice = new SendVoice();
        sendVoice.setChatId(message.getChatId());
        sendVoice.setVoice(new InputFile(oggFile));
        sendVoice.setDisableNotification(true);
        sendVoice.setProtectContent(true);
        Message voiceSentMessage = bot.execute(sendVoice);
        String voiceFileId = voiceSentMessage.getVoice().getFileId();
        log.info("File {} uploaded with id {}", oggFile.getName(), voiceFileId);
        return voiceFileId;
    }

    private File generateFileWithRandomName(String directory, String extension) {
        String fileName = UUID.randomUUID().toString();
        return new File(directory, fileName + extension);
    }

    private static void tryDeleteFile(File outputFile) {
        if (outputFile != null && outputFile.exists()) {
            boolean deleted = outputFile.delete();
            if (!deleted) {
                log.warn("Failed to delete temporary file: {}", outputFile.getAbsolutePath());
            }
        }
    }

    protected String generateFileWithRandomName(Long telegramUserId) {
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

    protected record UploadedFileName(String fileName, boolean isCustomName) {
    }
}
