package com.jintu.jintuaiagent.controller;

import com.jintu.jintuaiagent.agent.model.jinTuManus;
import com.jintu.jintuaiagent.app.JinTuApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-10
 * @Description:Ai调用
 * @Version: 1.0
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private JinTuApp jinTuApp;
    @Resource(name = "allTools")
    private ToolCallback[] toolCallbacks;
    @Resource
    private ChatModel dashscopeChatModel;
    @Autowired
    private jinTuManus jinTuManus;

    @GetMapping("/chat/sync")
    public String doChatWithJintuAppSync(String message, String chatId) {
        return jinTuApp.doChat(message, chatId);
    }

    // @GetMapping(value = "/chat/sse", produces =
    // MediaType.TEXT_EVENT_STREAM_VALUE)
    // public Flux<String> doChatWithJintuAppSSE(String message, String chatId) {
    // return jinTuApp.doChatByStream(message, chatId);
    // }

    @GetMapping(value = "/chat/sse", produces = "text/event-stream;charset=UTF-8")
    public Flux<ServerSentEvent<String>> doChatWithJintuAppSSE(String message, String chatId) {
        return jinTuApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    @GetMapping("/chat/sse/emitter")
    public SseEmitter doChatWithJintuAppSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        // 获取 Flux 数据流并直接订阅
        jinTuApp.doChatByStream(message, chatId)
                .subscribe(
                        // 处理每条消息
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        // 处理错误
                        emitter::completeWithError,
                        // 处理完成
                        emitter::complete);
        // 返回emitter
        return emitter;
    }

    /**
     * 流式调用Manus超级智能体
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message){
        jinTuManus jinTuManus= new jinTuManus(toolCallbacks, dashscopeChatModel);
        return jinTuManus.runStream(message);
    }

}
