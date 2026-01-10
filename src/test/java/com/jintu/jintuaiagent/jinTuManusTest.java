package com.jintu.jintuaiagent;

import com.jintu.jintuaiagent.agent.model.jinTuManus;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class jinTuManusTest {
  
    @Resource
    private jinTuManus jinTuManus;
  
    @Test
    void run() {  
        String userPrompt = """  
                我想入职锦途集团公司，怎么操作？
                并结合一些网络图片，制定一份详细的计划，  
                并以 PDF 格式输出""";  
        String answer = jinTuManus.run(userPrompt);
        Assertions.assertNotNull(answer);
        System.out.println(answer);
    }  
}
