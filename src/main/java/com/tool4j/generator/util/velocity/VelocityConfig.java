package com.tool4j.generator.util.velocity;

import com.tool4j.generator.entity.JavaDefinition;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @author Deng.Weiping
 * @since 2023/8/7 16:54
 */
@Data
@Accessors(chain = true)
public class VelocityConfig {

    /**
     * 模板名称
     */
    private String template;

    /**
     * 注释
     */
    private String comment;

    /**
     * 类名
     */
    private String upperClassName;

    /**
     * 实例名
     */
    private String lowClassName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 接口前缀
     */
    private String apiPathPrefix;

    /**
     * api方法路径
     */
    private String apiPath;

    /**
     * 包名
     */
    private String packagePath;

    /**
     * 作者
     */
    private String author;

    /**
     * 版本
     */
    private String since;

    /**
     * 字段
     */
    private List<JavaDefinition.Field> columns;

    /**
     * 方法名集合
     */
    private Map<String, MethodConfig> methods;

    /**
     * 方法名集合
     */
    private Map<String, MethodConfig> vueMethods;

    /**
     * 依赖：lombok
     */
    private boolean needLombok;

    /**
     * swagger
     */
    private boolean needSwagger;

    /**
     * validator
     */
    private boolean needValidator;

    private boolean errored;

    private String errorMsg;

    @Data
    @Accessors(chain = true)
    public static class MethodConfig {

        private String name;

        private String apiPath;

        private Boolean enabled;
    }
}
