package com.tool4j.generator.controller;

import com.tool4j.generator.entity.vo.Result;
import com.tool4j.generator.util.parser.TextParserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/api/text")
@Api(tags = "文本工具控制器")
public class TextParserController {

    /**
     * 文本导入
     *
     * @param file
     * @return 文件内容
     */
    @PostMapping("/importFile")
    @ApiOperation(value = "文本导入")
    public Result<String> importFile(MultipartFile file) {
        String fileType = TextParserUtil.getFileType(Objects.requireNonNull(file.getOriginalFilename()));
        if (TextParserUtil.canRead(fileType)) {
            return Result.ok(TextParserUtil.readText(file));
        }
        return Result.ok(null);
    }

}
