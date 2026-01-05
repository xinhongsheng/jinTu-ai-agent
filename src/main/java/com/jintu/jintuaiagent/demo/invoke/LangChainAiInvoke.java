package com.jintu.jintuaiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

public class LangChainAiInvoke {

    public static void main(String[] args) {
        ChatLanguageModel qwenModel = QwenChatModel.builder()
                .apiKey(TestApiKey.getApiKey())
                .modelName("qwen-max")
                .build();
        String answer = qwenModel.chat("你好，你是谁？会干什么？");
        System.out.println(answer);
    }
}
