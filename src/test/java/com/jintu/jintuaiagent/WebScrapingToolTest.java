package com.jintu.jintuaiagent;

import com.jintu.jintuaiagent.tools.WebScrapingTool;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebScrapingToolTest {

    @Test
    public void testScrapeWebPage() {
        WebScrapingTool tool = new WebScrapingTool();
        String url = "https://www.baidu.com";
        String result = tool.scrapeWebPage(url);
        System.out.println(result);
    }
}
