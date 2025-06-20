package com.bulhakov.controller.telegram;

import com.bulhakov.repository.interfaces.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultVoice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class InlineQueryHandler {

    private final FileRepository fileRepository;

    @Autowired
    public InlineQueryHandler(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public AnswerInlineQuery handleInlineQuery(InlineQuery inlineQuery) {
        List<InlineQueryResult> results = new ArrayList<>();
        Map<String, String> phrasesToShow = new HashMap<>();

        Long telegramUserId = inlineQuery.getFrom().getId();
        Map<String, String> phrases = fileRepository.getFilesForUser(telegramUserId);

        for (String phrase : phrases.keySet()) {
            phrasesToShow.put(phrase, phrases.get(phrase));
        }

        for (String phrase : phrasesToShow.keySet()) {
            InlineQueryResultVoice voiceResult = new InlineQueryResultVoice();
            voiceResult.setId(UUID.randomUUID().toString()); // Generate a unique ID for each result
            voiceResult.setTitle(phrase);
            voiceResult.setVoiceUrl(phrasesToShow.get(phrase)); // This must be a direct URL to the voice file
            // You might also want to set an inputMessageContent if clicking the voice should send a text message
            // voiceResult.setInputMessageContent(new InputTextMessageContent("Playing " + phrase.get("name")));
            results.add(voiceResult);
        }

        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setResults(results);
        answerInlineQuery.setCacheTime(1); // Set cache_time as in Python
        return answerInlineQuery;
    }
}
