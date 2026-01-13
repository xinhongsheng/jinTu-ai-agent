package com.jintu.jintuaiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import com.jintu.jintuaiagent.advisor.MyLoggerAdvisor;
import com.jintu.jintuaiagent.config.AliYunConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-06
 * @Description: 云知识库
 * @Version: 1.0
 */
@Configuration
@Slf4j
public class RagCloudAdvisorConfig {
    @Resource
    private AliYunConfig aliYunConfig;

    @Bean
    public Advisor ragCloudAdvisor(){
        String dashscopeApiKey = aliYunConfig.getApiKey();
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(dashscopeApiKey).build();
        final  String KNOWLEDGE_INDEX="锦途知识库";
        DashScopeDocumentRetriever documentRetriever = new DashScopeDocumentRetriever(dashScopeApi
                , DashScopeDocumentRetrieverOptions.builder()
                .withIndexName(KNOWLEDGE_INDEX)
                .build());

        return RetrievalAugmentationAdvisor.builder().documentRetriever(documentRetriever).build();
    }
}
