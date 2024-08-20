package com.tool4j.generator.util.parser;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class TextParserUtil {

    public static final List<String> canReadType = Arrays.asList("txt", "json", "sql", "xml", "yml", "yaml", "java", "js", "less", "sass", "html", "css", "py", "c", "cpp", "md");

    /**
     * 是否支持读取内容
     *
     * @param type
     * @return
     */
    public static boolean canRead(String type) {
        return canReadType.contains(type);
    }

    /**
     * 读取文件内容
     *
     * @param file
     * @return
     */
    public static String readText(MultipartFile file) {
        String type = parseFileType(file.getOriginalFilename());
        if (canRead(type)) {
            try (InputStream input = file.getInputStream()) {
                BufferedReader reader = IoUtil.getUtf8Reader(input);
                StringBuffer buffer = new StringBuffer();
                reader.lines().forEach(line -> buffer.append(line).append(StrUtil.LF));
                reader.close();
                return buffer.toString();
            } catch (Exception e) {
                log.error("文件读取失败");
            }
        }
        return null;
    }

    /**
     * 获取文件类型
     *
     * @param fileName
     * @return
     */
    public static String getFileType(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index < 0) {
            return "unknown";
        }
        return fileName.substring(index + 1);
    }

    /**
     * 解析文件类型
     *
     * @param fileName
     * @return
     */
    public static String parseFileType(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return "unknown";
        }
        int index = fileName.lastIndexOf(".");
        if (index < 0) {
            return "unknown";
        }
        return fileName.substring(index + 1);
    }

}
