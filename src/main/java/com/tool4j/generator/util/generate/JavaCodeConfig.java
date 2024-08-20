package com.tool4j.generator.util.generate;

import com.tool4j.generator.util.velocity.VelocityConfig;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class JavaCodeConfig {

    /**
     * 原始文本
     */
    private String input;

    /**
     * 解析器类型
     */
    private String parserType;

    /**
     * 原文本类型
     */
    private String sourceType;

    /**
     * ORM框架
     */
    public static final String ORM_TYPE_JPA = "jpa";
    public static final String ORM_TYPE_MYBATIS = "mybatis";
    public static final String ORM_TYPE_MYBATIS_PLUS = "mybatisPlus";

    /**
     * 需生成的代码类：controller、service、serviceImpl、javaBean、dao、xml
     */
    public static final String CLAZZ_KEY_CONTROLLER = "controller";
    public static final String CLAZZ_KEY_SERVICE = "service";
    public static final String CLAZZ_KEY_SERVICE_IMPL = "serviceImpl";
    public static final String CLAZZ_KEY_JAVABEAN = "javaBean";
    public static final String CLAZZ_KEY_DOMAIN = "domain";
    public static final String CLAZZ_KEY_ENTITY = "entity";
    public static final String CLAZZ_KEY_MAPPER = "mapper";
    public static final String CLAZZ_KEY_MAPPER_XML = "mapperXml";

    /**
     * 自定义包名：控制类、逻辑类、实体类、持久化类、持久化xml
     */
    public static final String PACKAGE_KEY_CONTROLLER = "controller";
    public static final String PACKAGE_KEY_SERVICE = "service";
    public static final String PACKAGE_KEY_SERVICE_IMPL = "service.impl";
    public static final String PACKAGE_KEY_JAVABEAN = "entity";
    public static final String PACKAGE_KEY_MAPPER = "mapper";
    public static final String PACKAGE_KEY_MAPPER_XML = "mapper";

    /**
     * 依赖：lombok、swagger、validator
     */
    public static final String DEPENDENCY_KEY_LOMBOK = "lombok";
    public static final String DEPENDENCY_KEY_SWAGGER = "swagger";
    public static final String DEPENDENCY_KEY_VALIDATOR = "validator";

    /**
     * 包名
     */
    private String basePackage;

    /**
     * 作者名称
     */
    private String author;

    /**
     * 接口风格
     */
    private String restType;

    /**
     * 接口前缀
     */
    private String apiPathPrefix;

    /**
     * 表前缀
     */
    private String tableNamePrefix;

    /**
     * 持久层框架
     */
    private String ormType;

    /**
     * 方法配置集合
     */
    private Map<String, VelocityConfig.MethodConfig> methods;

    /**
     * 方法配置集合
     */
    private Map<String, VelocityConfig.MethodConfig> vueMethods;

    /**
     * 依赖：lombok、swagger、validator
     */
    private List<String> dependencies;

    /**
     * 字段集合
     */
    private List<String> fields;
}
