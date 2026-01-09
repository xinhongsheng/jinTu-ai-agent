package com.jintu.jintuaiagent;

import com.jintu.jintuaiagent.tools.ResourceDownloadTool;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ResourceDownloadToolTest {

    @Test
    public void testDownloadResource() {
        ResourceDownloadTool tool = new ResourceDownloadTool();
        String url = "";
        String fileName = "logo.png";
        String result = tool.downloadResource(url, fileName);
        System.out.println(result);
    }
}
