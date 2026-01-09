package com.jintu.jintuimagesearchmcp;

import com.jintu.jintuimagesearchmcp.tools.ImageSearchTool;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ImageSearchToolTest {

    @Resource
    private ImageSearchTool imageSearchTool;

    @Test
    void searchImage() {
        String result = imageSearchTool.searchImage("computer");
        System.out.println(result);
    }
}
