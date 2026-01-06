package com.jintu.jintuaiagent.advisor;

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisorChain;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * 违禁词校验 advisor.
 * Expects a list of forbidden words in request userParams, or falls back to defaults.
 */
public class ForbiddenWordsAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    public static final String FORBIDDEN_WORDS_KEY = "forbiddenWords";

    private final List<String> defaultForbiddenWords;

    public ForbiddenWordsAdvisor(List<String> defaultForbiddenWords) {
        this.defaultForbiddenWords = normalize(defaultForbiddenWords);
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        checkForbidden(advisedRequest);
        return chain.nextAroundCall(advisedRequest);
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        checkForbidden(advisedRequest);
        return chain.nextAroundStream(advisedRequest);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    private void checkForbidden(AdvisedRequest advisedRequest) {
        String userText = advisedRequest.userText();
        if (userText == null || userText.isBlank()) {
            return;
        }

        List<String> forbiddenWords = resolveForbiddenWords(advisedRequest);
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

    private List<String> resolveForbiddenWords(AdvisedRequest advisedRequest) {
        Object raw = advisedRequest.userParams().get(FORBIDDEN_WORDS_KEY);
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
