package com.jintu.jintuaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.jintu.jintuaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-10
 * @Description: PDF生成工具
 * @Version: 1.0
 */
public class PdfGenerationTool {

    private final String PDF_DIR = FileConstant.FILE_SAVE_DIR + "/pdf";

    @Tool(description = "Generate a PDF document with the given content")
    public String generatePdf(
            @ToolParam(description = "Name of the PDF file to generate (without .pdf extension)") String fileName,
            @ToolParam(description = "Content to write to the PDF document") String content) {
        String filePath = PDF_DIR + "/" + fileName + ".pdf";
        try {
            // 创建目录
            FileUtil.mkdir(PDF_DIR);

            // 创建PDF文档
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // 使用默认字体（支持基本ASCII字符）
            // 如需中文支持，需要添加中文字体
            Paragraph paragraph = new Paragraph(content);
            document.add(paragraph);

            document.close();

            return "PDF generated successfully: " + filePath;
        } catch (Exception e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }
}
