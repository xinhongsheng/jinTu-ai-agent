package com.jintu.jintuaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.jintu.jintuaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-08
 * @Description:文件操作
 * @Version: 1.0
 */
public class FileOperationTool {

    private final String FILE_DIR= FileConstant.FILE_SAVE_DIR+"/file";
    @Tool(description = "read content from a file")
    public String readFile(@ToolParam(description = "name of the file to read") String fileName){
        String filePath=FILE_DIR+"/"+fileName;
        try{
            return FileUtil.readUtf8String(filePath);
        }catch (Exception e){
            return "Error reading file:"+e.getMessage();
        }
    }

    @Tool(description = "write content to a file")
    public String writeFile(@ToolParam(description = "name of the file to write") String fileName,@ToolParam(description = "content to write to the file")String content){
        String filePath=FILE_DIR+"/"+fileName;
        try{
            //创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content,filePath);
            return "File written successfully,"+filePath;
        }catch (Exception e){
            return "Error writing file:"+e.getMessage();
        }
    }
}
