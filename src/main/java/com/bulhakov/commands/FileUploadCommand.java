package com.bulhakov.commands;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.repository.interfaces.FileRepository;
import com.bulhakov.util.LocalizationManager;
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

@Component
@CommandMapping(name = COMMAND_NAME)
public class FileUploadCommand extends AbstractCommand {

    public static final String COMMAND_NAME = "/upload";

    private final FileRepository fileRepository;

    @Autowired
    public FileUploadCommand(LocalizationManager localizationManager, FileRepository fileRepository) {
        super(localizationManager);
        this.fileRepository = fileRepository;
    }

    @Override
    public void processUpdate(Update update, TelegramLongPollingBot controller) {
        Message message = update.getMessage();

        Optional<String> fileIdOptional = tryExtractingFileId(message);
        fileIdOptional.ifPresentOrElse(fileId -> {

            Long telegramUserId = message.getFrom().getId();
            Pair<String, Boolean> filenameGenerationResult = getFileName(message, telegramUserId);
            fileRepository.storeFile(telegramUserId, filenameGenerationResult.getLeft(), fileId);

            String successText = getFileUploadedText(filenameGenerationResult.getLeft(), filenameGenerationResult.getRight());
            SendMessage sendMessage = getAnswer(message, successText);
            execute(controller, sendMessage);

            //GetFile getFile = new GetFile();
            //getFile.setFileId(fileId);
            //tryDownloadFile(controller, message, getFile);
        }, () -> {
            String errorText = localizationManager.getStringFromResource("FILE_UPLOAD_ERROR");
            SendMessage sendMessage = getAnswer(message, errorText);
            execute(controller, sendMessage);
        });
    }

    private Pair<String, Boolean> getFileName(Message message, Long telegramUserId) {
        String messageContent = message.getCaption().substring(COMMAND_NAME.length()).stripLeading();
        if (!messageContent.isEmpty()) {
            return Pair.of(messageContent, true);
        }

        // Generate a unique file name based on a random UUID
        String generatedId = null;
        while (generatedId == null) {
            String randomId = UUID.randomUUID().toString();
            String randomIdShort = randomId.substring(0, randomId.indexOf("-"));
            if (fileRepository.getFileForUser(telegramUserId, randomIdShort).isEmpty()) {
                generatedId = randomIdShort;
            }
        }
        return Pair.of(generatedId, false);
    }

    private String getFileUploadedText(String fileName, boolean isCustomName) {
        String basicMessage = localizationManager.getStringFromResource("FILE_UPLOAD_SUCCESS");
        return (isCustomName)
                ? "%s. File name: %s".formatted(basicMessage, fileName)
                : "%s. File name: %s. You can change it using the /rename command".formatted(basicMessage, fileName);
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
            System.out.println(downloaded.getAbsolutePath());
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
