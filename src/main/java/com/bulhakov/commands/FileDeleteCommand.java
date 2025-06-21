package com.bulhakov.commands;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.repository.interfaces.FileRepository;
import com.bulhakov.util.LocalizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import static com.bulhakov.commands.FileDeleteCommand.COMMAND_NAME;

@Component
@CommandMapping(name = COMMAND_NAME)
public class FileDeleteCommand extends AbstractCommand {

    public static final String COMMAND_NAME = "/delete";

    private final FileRepository fileRepository;

    @Autowired
    public FileDeleteCommand(LocalizationManager localizationManager, FileRepository fileRepository) {
        super(localizationManager);
        this.fileRepository = fileRepository;
    }

    @Override
    public void processUpdate(Update update, TelegramLongPollingBot controller) {
        Message message = update.getMessage();
        String filename;
        String messageContent = message.getText().substring(COMMAND_NAME.length()).trim();
        if (!messageContent.isEmpty()) {
            filename = messageContent;
        } else {
            String errorText = localizationManager.getStringFromResource("FILE_DELETE_INVALID_COMMAND");
            SendMessage sendMessage = getAnswer(message, errorText);
            execute(controller, sendMessage);
            return;
        }

        Long telegramUserId = message.getFrom().getId();

        Optional<String> file = fileRepository.getFileForUser(telegramUserId, filename);
        file.ifPresentOrElse(existingFileName -> {
            fileRepository.deleteFile(telegramUserId, filename);
            String successText = localizationManager.getStringFromResource("FILE_DELETE_SUCCESS");
            SendMessage sendMessage = getAnswer(message, successText);
            execute(controller, sendMessage);
        }, () -> {
            String errorText = localizationManager.getStringFromResource("FILE_DELETE_NOT_FOUND")
                    + ": " + filename;
            SendMessage sendMessage = getAnswer(message, errorText);
            execute(controller, sendMessage);
        });
    }
}
