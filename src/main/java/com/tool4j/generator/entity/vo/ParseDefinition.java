package com.tool4j.generator.entity.vo;

import com.tool4j.generator.util.velocity.VelocityConfig;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 文本生成配置
 */
@Data
@Accessors(chain = true)
public class ParseDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 输入内容
     */
    private String input;

    /**
     * 解析器类型
     */
    private String parserType;

    /**
     * 源类型
     */
    private String sourceType;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 方法集合
     */
    private Map<String, VelocityConfig.MethodConfig> methods;

    /**
     * 手动选择的字段集合
     */
    private List<String> fields;

    /**
     * 接口前缀
     */
    private String apiPath;

    /**
     * 表前缀
     */
    private String tablePrefix;
}
