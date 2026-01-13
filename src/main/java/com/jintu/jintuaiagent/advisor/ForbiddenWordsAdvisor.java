package com.jintu.jintuaiagent.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * 违禁词校验 advisor.
 * Expects a list of forbidden words in request userParams, or falls back to
 * defaults.
 */
public class ForbiddenWordsAdvisor implements CallAdvisor, StreamAdvisor {

    public static final String FORBIDDEN_WORDS_KEY = "forbiddenWords";

    private final List<String> defaultForbiddenWords;

    public ForbiddenWordsAdvisor(List<String> defaultForbiddenWords) {
        this.defaultForbiddenWords = normalize(defaultForbiddenWords);
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain chain) {
        checkForbidden(chatClientRequest);
        return chain.nextCall(chatClientRequest);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain chain) {
        checkForbidden(chatClientRequest);
        return chain.nextStream(chatClientRequest);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    private void checkForbidden(ChatClientRequest chatClientRequest) {
        String userText = chatClientRequest.prompt().getUserMessage().getText();
        if (userText == null || userText.isBlank()) {
            return;
        }

        List<String> forbiddenWords = resolveForbiddenWords(chatClientRequest);
        if (forbiddenWords.isEmpty()) {
            return;
        }

        String text = userText.toLowerCase(Locale.ROOT);
        for (String word : forbiddenWords) {
            if (!word.isEmpty() && text.contains(word)) {
                throw new IllegalStateException("Forbidden content detected");
            }
        }
    }

    private List<String> resolveForbiddenWords(ChatClientRequest chatClientRequest) {
        Object raw = chatClientRequest.context().get(FORBIDDEN_WORDS_KEY);
        if (raw == null) {
            return defaultForbiddenWords;
        }
        if (raw instanceof Collection<?> collection) {
            List<String> words = new ArrayList<>();
            for (Object item : collection) {
                if (item != null) {
                    words.add(item.toString());
                }
            }
            return normalize(words);
        }
        return normalize(List.of(raw.toString()));
    }

    private List<String> normalize(List<String> words) {
        List<String> normalized = new ArrayList<>();
        if (words == null) {
            return normalized;
        }
        for (String word : words) {
            if (word != null && !word.isBlank()) {
                normalized.add(word.trim().toLowerCase(Locale.ROOT));
            }
        }
        return normalized;
    }
}
