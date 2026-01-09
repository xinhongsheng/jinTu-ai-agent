package com.jintu.jintuaiagent;

import com.jintu.jintuaiagent.tools.WebSearchTool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebSearchToolTest {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Test
    public void testSearchWeb() {
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        String result = webSearchTool.searchWeb("Java");
        System.out.println(result);
    }
}
