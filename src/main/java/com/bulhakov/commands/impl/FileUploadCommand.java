package com.bulhakov.commands.impl;

import com.bulhakov.annotations.CommandMapping;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
@CommandMapping(name = "/upload")
public class FileUploadCommand extends AbstractCommand {

    @Override
    public void processUpdate(Update update, TelegramLongPollingBot controller) {
        Message message = update.getMessage();

        Optional<String> fileIdOptional = tryExtractingFileId(message);
        fileIdOptional.ifPresentOrElse(fileId -> {
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);
            tryDownloadFile(controller, message, getFile);
        }, () -> {
            String errorText = localizationManager.getStringFromResource("FILE_UPLOAD_ERROR");
            SendMessage sendMessage = getAnswer(message, errorText);
            execute(controller, sendMessage);
        });
    }

    private Optional<String> tryExtractingFileId(Message message) {
        //TODO support different file types
        if (message.hasAudio()) {
            return Optional.ofNullable(message.getAudio().getFileId());
        } else if (message.hasDocument()) {
            return Optional.ofNullable(message.getDocument().getFileId());
        } else if (message.hasVoice()) {
            return Optional.ofNullable(message.getVoice().getFileId());
        }else {
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
