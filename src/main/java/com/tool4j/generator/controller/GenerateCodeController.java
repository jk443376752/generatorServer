package com.tool4j.generator.controller;

import com.tool4j.generator.entity.TemplateInfo;
import com.tool4j.generator.entity.vo.Result;
import com.tool4j.generator.service.GenerateCodeService;
import com.tool4j.generator.service.TemplateInfoService;
import com.tool4j.generator.util.generate.JavaCodeConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器控制器
 *
 * @since 0.0.1
 */
@RestController
@Slf4j
@RequestMapping("/api/codeGenerate")
@Api(tags = "代码生成控制器")
public class GenerateCodeController {

    @Autowired
    private GenerateCodeService generateCodeService;

    @Autowired
    private TemplateInfoService templateInfoService;

    /**
     * 代码生成
     *
     * @return
     */
    @PostMapping("/generateCode")
    @ApiOperation(value = "代码生成")
    public Result<Map<String, Object>> generateCode(@Validated @NotNull @RequestBody JavaCodeConfig params) {
        log.info("\n>>>>>>>>>>>>>>> Author: {}, BasePackage: {}, parser={}, ormType={}", params.getAuthor(), params.getBasePackage(), params.getParserType(), params.getOrmType());
        return Result.ok(generateCodeService.generate(params));
    }

    /**
     * 查询全部模板
     *
     * @return
     */
    @ApiOperation(value = "查询全部模板", notes = "查询全部模板", produces = "application/json")
    @ApiResponses({@ApiResponse(code = 200, message = "查询成功")})
    @PostMapping("/findAll")
    public Result<List<TemplateInfo>> findAll() {
        List<TemplateInfo> result = templateInfoService.findAll();
        return Result.ok(result);
    }

}
