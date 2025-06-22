package com.bulhakov.controller.telegram;

import com.bulhakov.model.File;
import com.bulhakov.model.User;
import com.bulhakov.services.FileService;
import com.bulhakov.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultVoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class InlineQueryHandler {

    private final UserService userService;
    private final FileService fileService;

    @Autowired
    public InlineQueryHandler(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    public AnswerInlineQuery handleInlineQuery(InlineQuery inlineQuery) {
        List<InlineQueryResult> results = new ArrayList<>();

        Long telegramUserId = inlineQuery.getFrom().getId();
        User user = userService.findUserByExternalId(telegramUserId);
        List<File> userFiles = fileService.getFilesForUser(user.getId());
        Map<String, String> fileNamesToExternalFileIds = userFiles.stream()
                .collect(Collectors.toMap(File::getFileName, File::getExternalFileId));

        for (String fileName : fileNamesToExternalFileIds.keySet()) {
            InlineQueryResultVoice voiceResult = new InlineQueryResultVoice();
            voiceResult.setId(UUID.randomUUID().toString()); // Generate a unique ID for each result
            voiceResult.setTitle(fileName);
            voiceResult.setVoiceUrl(fileNamesToExternalFileIds.get(fileName)); // This must be a direct URL to the voice file
            // You might also want to set an inputMessageContent if clicking the voice should send a text message
            // voiceResult.setInputMessageContent(new InputTextMessageContent("Playing " + fileName.get("username")));
            results.add(voiceResult);
        }

        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setResults(results);
        answerInlineQuery.setCacheTime(1); // Set cache_time as in Python
        return answerInlineQuery;
    }
}
