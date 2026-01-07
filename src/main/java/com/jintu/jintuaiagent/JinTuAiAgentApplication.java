package com.jintu.jintuaiagent;

import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class JinTuAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(JinTuAiAgentApplication.class, args);
    }

}
