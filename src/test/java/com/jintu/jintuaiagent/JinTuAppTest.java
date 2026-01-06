package com.jintu.jintuaiagent;

import com.jintu.jintuaiagent.app.JinTuApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-05
 * @Description:
 * @Version: 1.0
 */
@SpringBootTest
public class JinTuAppTest {
    @Resource
    private JinTuApp jinTuApp;

    @Test
    void Test(){
        String chatId= UUID.randomUUID().toString();
        String message = "你好，我是锦途开发人员Re";
        String answer = jinTuApp.doChat(message,chatId);
        System.out.println("第一次："+answer);


        message = "你好，你知道锦途是什么公司吗?";
        answer = jinTuApp.doChat(message,chatId);
        System.out.println("第二次："+answer);

        message = "你还记得我是谁吗？刚才和你说过";
        answer = jinTuApp.doChat(message,chatId);
        System.out.println("第三次："+answer);
    }

    @Test
    void TestWithReport(){
        String chatId= UUID.randomUUID().toString();
        String message = "你好，我是锦途开发人员Re,我遇到一个bug，不知道怎么解决？   ";
        JinTuApp.jinTuReport jinTuReport = jinTuApp.doChatWithReport(message, chatId);

    }
}
