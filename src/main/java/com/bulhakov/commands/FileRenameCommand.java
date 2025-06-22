package com.bulhakov.commands;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.model.File;
import com.bulhakov.services.FileService;
import com.bulhakov.util.LocalizationManager;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bulhakov.commands.FileRenameCommand.COMMAND_NAME;

@Component
@CommandMapping(name = COMMAND_NAME)
public class FileRenameCommand extends AbstractCommand {

    public static final String COMMAND_NAME = "/rename";

    private final FileService fileService;

    @Autowired
    public FileRenameCommand(LocalizationManager localizationManager, FileService fileService) {
        super(localizationManager);
        this.fileService = fileService;
    }

    @Override
    public void processUpdate(Update update, TelegramLongPollingBot bot) {
        // pair oldFilename - newFilename
        Message message = update.getMessage();
        Pair<String, String> filenames;
        try {
            filenames = extractFilenamesOrNull(message.getText());
        } catch (IllegalArgumentException e) {
            String errorText = localizationManager.getStringFromResource("FILE_RENAME_INVALID_COMMAND");
            SendMessage sendMessage = getAnswer(message, errorText);
            execute(bot, sendMessage);
            return;
        }
        Long telegramUserId = message.getFrom().getId();

        tryRenameFile(bot, telegramUserId, filenames, message);
    }

    private void tryRenameFile(TelegramLongPollingBot bot, Long telegramUserId, Pair<String, String> filenames, Message message) {
        Optional<File> file = fileService.getFileForUser(telegramUserId, filenames.getLeft());
        file.ifPresentOrElse(existingFile -> {

            if (fileService.getFileForUser(telegramUserId, filenames.getRight()).isPresent()) {
                String errorText = localizationManager.getStringFromResource("FILE_RENAME_ALREADY_EXISTS")
                        + ": " + filenames.getRight();
                SendMessage sendMessage = getAnswer(message, errorText);
                execute(bot, sendMessage);
                return;
            }

            fileService.renameFile(telegramUserId, filenames.getLeft(), filenames.getRight());
            String successText = localizationManager.getStringFromResource("FILE_RENAME_SUCCESS");
            SendMessage sendMessage = getAnswer(message, successText);
            execute(bot, sendMessage);
        }, () -> {
            String errorText = localizationManager.getStringFromResource("FILE_RENAME_NOT_FOUND")
                    + ": " + filenames.getLeft();
            SendMessage sendMessage = getAnswer(message, errorText);
            execute(bot, sendMessage);
        });
    }

    private Pair<String, String> extractFilenamesOrNull(String message) {
        String messageContent = message.substring(COMMAND_NAME.length()).stripLeading();
        if (messageContent.isEmpty()) {
            throw new IllegalArgumentException("Command must contain filenames");
        }

        String regex = "^from:(.+)\\s+to:(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(messageContent);

        if (matcher.matches()) {
            String oldName = matcher.group(1).trim(); // Trim to remove potential leading/trailing spaces
            String newName = matcher.group(2).trim(); // Trim to remove potential leading/trailing spaces
            return Pair.of(oldName, newName);
        } else {
            throw new IllegalArgumentException("Invalid command format");
        }
    }
}
