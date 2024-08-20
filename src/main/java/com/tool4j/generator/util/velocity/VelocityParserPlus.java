package com.tool4j.generator.util.velocity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * velocity模板渲染工具类
 *
 * @author Deng.Weiping
 * @since 2023/7/25 12:03
 */
@Slf4j
public class VelocityParserPlus {

    public static final String JAVA_TYPE_INT = "Integer";
    public static final String JAVA_TYPE_BIGINT = "Long";
    public static final String JAVA_TYPE_NUM = "float";
    public static final String JAVA_TYPE_ARRAY = "List";
    private static final String JAVA_TYPE_BOOLEAN = "Boolean";
    private static final String JAVA_TYPE_MAP = "Map";
    public static final String JAVA_TYPE_STRING = "String";
    public static final String JAVA_TYPE_DATE = "Date";

    /**
     * 模板校验
     *
     * @param template
     */
    public static void verify(String template) {
        //设置velocity资源加载器
        initVelocity();
        //渲染模板
        Velocity.getTemplate(template, "UTF-8");
    }

    public static String execute(VelocityConfig config) {
        String result;
        try {
            //设置velocity资源加载器
            initVelocity();
            //封装模板数据
            VelocityContext context = getVelocityContext(config);
            //渲染模板
            Template tpl = Velocity.getTemplate(config.getTemplate(), "UTF-8");
            StringWriter sw = new StringWriter();
            tpl.merge(context, sw);
            result = sw.toString();
            IOUtils.closeQuietly(sw);
        } catch (Exception e) {
            throw new RuntimeException("渲染模板失败:", e);
        }
        return result;
    }

    private static void initVelocity() {
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "com.tool4j.generator.util.velocity.VelocityTemplateLoader");
        Velocity.init(prop);
    }

    /**
     * 加载VelocityContext
     *
     * @param config
     * @return
     */
    private static VelocityContext getVelocityContext(VelocityConfig config) {
        Map<String, Object> map = BeanUtil.beanToMap(config);
        return new VelocityContext(map);
    }

    /**
     * 驼峰转首字母大写
     *
     * @param camelCase
     * @return
     */
    public static String getUpperName(String camelCase) {
        if (StrUtil.isBlank(camelCase)) {
            return camelCase;
        }
        return String.format("%s%s", camelCase.substring(0, 1).toUpperCase(), camelCase.substring(1));
    }


    /**
     * 驼峰转首字母小写
     *
     * @param camelCase
     * @return
     */
    public static String getLowerName(String camelCase) {
        if (StrUtil.isBlank(camelCase)) {
            return camelCase;
        }
        return String.format("%s%s", camelCase.substring(0, 1).toLowerCase(), camelCase.substring(1));
    }

    public static String toJavaType(String dataType) {
        if (StrUtil.isBlank(dataType)) {
            return JAVA_TYPE_STRING;
        }
        switch (dataType.toUpperCase()) {
            case "INT":
            case "TINYINT":
            case "SMALLINT":
            case "MEDIUMINT":
            case "INTEGER":
                return JAVA_TYPE_INT;
            case "BIGINT":
                return JAVA_TYPE_BIGINT;
            case "FLOAT":
            case "DOUBLE":
            case "DECIMAL":
            case "NUMBER":
                return JAVA_TYPE_NUM;
            case "ENUM":
            case "SET":
            case "ARRAY":
                return JAVA_TYPE_ARRAY;
            case "BOOLEAN":
                return JAVA_TYPE_BOOLEAN;
            case "OBJECT":
                return JAVA_TYPE_MAP;
            case "DATE":
            case "DATETIME":
            case "TIMESTAMP":
                return JAVA_TYPE_DATE;
            default:
                return JAVA_TYPE_STRING;
        }
    }
}
