package com.jintu.jintuaiagent.config;

import com.jintu.jintuaiagent.tools.*;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-10
 * @Description: 工具注册配置类
 * @Version: 1.0
 */
@Configuration
public class ToolRegistration {

    @Value("${search.api.key:}")
    private String searchApiKey;

    @Bean("allTools")
    public ToolCallback[] allTools() {
        // 创建各个工具实例
        FileOperationTool fileOperationTool = new FileOperationTool();
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        PdfGenerationTool pdfGenerationTool = new PdfGenerationTool();
        TerminateTool terminateTool = new TerminateTool();

        return ToolCallbacks.from(
                fileOperationTool,
                webSearchTool,
                webScrapingTool,
                resourceDownloadTool,
                terminalOperationTool,
                pdfGenerationTool,
                terminateTool);
    }
}
