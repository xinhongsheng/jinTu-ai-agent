package com.jintu.jintuaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-06
 * @Description: 文档提取器
 * @Version: 1.0
 */
@Slf4j
@Component
public class DocumentLoader {
    private final ResourcePatternResolver resourcePatternResolver;

    DocumentLoader(ResourcePatternResolver resourcePatternResolver){
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public List<Document> loadText(){
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.pdf");
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
                List<Document> documents = tikaDocumentReader.read();
                if (!documents.isEmpty()) {
                    allDocuments.addAll(documents);
                    log.info("Loaded {} documents from {}", documents.size(), fileName);
                } else {
                    log.warn("No documents extracted from {}", fileName);
                }
            }
        } catch (Exception e) {
            log.error("Failed to load documents from classpath:document/*.pdf", e);
        }
        return allDocuments;
    }

}
