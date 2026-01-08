package com.jintu.jintuaiagent;

import com.jintu.jintuaiagent.tools.FileOperationTool;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-08
 * @Description:
 * @Version: 1.0
 */
@SpringBootTest
public class FileOperationTest {
    @Test
    public void testReadFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "test.txt";
        String content = tool.readFile(fileName);
        System.out.println(content);
    }

    @Test
    public void testWriteFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "test.txt";
        String content = "hello world";
        tool.writeFile(fileName, content);
    }
}
