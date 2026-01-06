package com.jintu.jintuaiagent.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-06
 * @Description: 向量数据库配置
 * @Version: 1.0
 */
@Configuration
@Slf4j
public class VectorStoreConfig {
    @Resource
    private DocumentLoader documentLoader;

    @Bean
    VectorStore myVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        //加载文档
        List<Document> documents = documentLoader.loadText();
        if (documents.isEmpty()) {
            log.warn("No documents loaded; vector store will be empty");
            return simpleVectorStore;
        }
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }
}
