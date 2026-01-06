package com.jintu.jintuaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-05
 * @Description: 自定义Advisor，日志记录
 * @Version: 1.0
 */
@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {


    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private AdvisedRequest before(AdvisedRequest request){
        log.info("AI Request :{}", request.userText());
        return request;
    }
    private void observeAfter(AdvisedResponse response){
        log.info("AI Response :{}", response.response().getResult().getOutput().getText());
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest,CallAroundAdvisorChain  chain){
        advisedRequest=this.before(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        this.observeAfter(advisedResponse);
        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest=this.before(advisedRequest);
        Flux<AdvisedResponse> advisedResponse = chain.nextAroundStream(advisedRequest);
        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponse, this::observeAfter);
    }
}
