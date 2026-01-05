package com.jintu.jintuaiagent.demo.invoke;

import com.jintu.jintuaiagent.JinTuAiAgentApplication;
import com.jintu.jintuaiagent.config.AliYunConfig;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public final class TestApiKey {
    private TestApiKey() {
    }

    public static String getApiKey() {
        String envKey = System.getenv("DASHSCOPE_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            return envKey;
        }

        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(JinTuAiAgentApplication.class)
                .web(WebApplicationType.NONE)
                .run()) {
            AliYunConfig config = context.getBean(AliYunConfig.class);
            String apiKey = config.getApiKey();
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalStateException("Aliyun api-key is empty. Set aliyun.key.api-key or DASHSCOPE_API_KEY.");
            }
            return apiKey;
        }
    }
}
