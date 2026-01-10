package com.jintu.jintuaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.jintu.jintuaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-10
 * @Description: 终端操作工具
 * @Version: 1.0
 */
public class TerminalOperationTool {

    private final String WORK_DIR = FileConstant.FILE_SAVE_DIR;

    @Tool(description = "Execute a terminal/shell command and return the output")
    public String executeCommand(@ToolParam(description = "The command to execute in terminal") String command) {
        try {
            // 确保工作目录存在
            FileUtil.mkdir(WORK_DIR);

            ProcessBuilder processBuilder = new ProcessBuilder();
            // 根据操作系统选择不同的命令执行方式
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                processBuilder.command("cmd.exe", "/c", command);
            } else {
                processBuilder.command("sh", "-c", command);
            }
            processBuilder.directory(new java.io.File(WORK_DIR));
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "Command executed successfully:\n" + output.toString();
            } else {
                return "Command execution failed with exit code " + exitCode + ":\n" + output.toString();
            }
        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        }
    }
}
