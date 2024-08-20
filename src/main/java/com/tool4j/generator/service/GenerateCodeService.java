package com.tool4j.generator.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.tool4j.generator.common.ParserEnum;
import com.tool4j.generator.entity.JavaDefinition;
import com.tool4j.generator.entity.TemplateInfo;
import com.tool4j.generator.entity.vo.ParserResult;
import com.tool4j.generator.entity.vo.TreeVO;
import com.tool4j.generator.util.generate.JavaCodeConfig;
import com.tool4j.generator.util.parser.SqlParserLoader;
import com.tool4j.generator.util.parser.sql.DbTableDefinition;
import com.tool4j.generator.util.parser.sql.common.CommonSqlParserUtil;
import com.tool4j.generator.util.parser.sql.common.SqlUtil;
import com.tool4j.generator.util.velocity.VelocityConfig;
import com.tool4j.generator.util.velocity.VelocityParserPlus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 代码生成器逻辑类
 *
 * @since 0.0.1
 */
@Component
@Slf4j
public class GenerateCodeService {

    @Autowired
    private TemplateInfoService templateInfoService;

    /**
     * 代码生成入口类
     *
     * @param params
     * @return
     */
    public Map<String, Object> generate(JavaCodeConfig params) {
        /*
          生成代码
         */
        List<ParserResult> results = execute(params);
        Map<String, Object> map = new HashMap<>();
        map.put("results", results);
        TreeVO root;
        root = new TreeVO().setKey(params.getBasePackage()).setTitle(params.getBasePackage());
        String basePath = params.getBasePackage().replace(".", "/") + "/";
        for (ParserResult parserResult : results) {
            String[] dirArr = parserResult.getFileName().replace(basePath, "").split("/");
            buildFileTree(root, dirArr, 0);
        }
        map.put("fileTree", root);
        if (CollUtil.isEmpty(results)) {
            log.error("SQL解析失败，建议您切换SQL解析器重试，Input = {}", params.getInput());
            throw new IllegalArgumentException("SQL解析失败，建议您切换SQL解析器重试");
        }
        if (results.stream().anyMatch(ParserResult::isErrored)) {
            List<ParserResult> errors = results.stream().filter(ParserResult::isErrored).collect(Collectors.toList());
            log.info(">>>>>>>>>>>>>>> generate code exists error: total={}, error={}，Input = {}", results.size(), errors.size(), params.getInput());
        } else {
            log.info(">>>>>>>>>>>>>>> generate code all succeed.");
        }
        return map;
    }

    /**
     * 生成代码
     *
     * @param config {@link JavaCodeConfig}
     * @return {@link List<ParserResult>}
     */
    private List<ParserResult> execute(JavaCodeConfig config) {
        List<ParserResult> results = new ArrayList<>();
        List<VelocityConfig> list = preParser(config);
        TemplateInfo templateInfo = templateInfoService.getTemplateInfo(config.getOrmType());
        Assert.notNull(templateInfo, "模板不存在");
        list.forEach(velocityConfig -> {
            if (velocityConfig.isErrored()) {
                String fileName = config.getBasePackage().replace(".", "/") + "/errors/" + velocityConfig.getTableName();
                if (results.stream().filter(item -> item.getFileName().equalsIgnoreCase(fileName)).count() > 0) {
                    return;
                }
                results.add(new ParserResult(fileName, SqlParserLoader.TEXT_TYPE_JAVA, velocityConfig.getErrorMsg(), true));
                return;
            }
            velocityConfig.setMethods(config.getMethods())
                    .setVueMethods(config.getVueMethods())
                    .setApiPath(velocityConfig.getApiPath())
                    .setAuthor(config.getAuthor())
                    .setSince(DateUtil.now())
                    .setPackagePath(config.getBasePackage());
            if (CollUtil.isNotEmpty(config.getDependencies())) {
                velocityConfig.setNeedLombok(config.getDependencies().contains("lombok"))
                        .setNeedSwagger(config.getDependencies().contains("swagger"))
                        .setNeedValidator(config.getDependencies().contains("validator"));
            }
            for (TemplateInfo.FileInfo fileInfo : templateInfo.getFiles()) {
                velocityConfig.setTemplate(String.format("%s/%s/%s", templateInfo.getPath(), fileInfo.getFilePath(), fileInfo.getFileName()));
                String fileName;
                if (StrUtil.isNotBlank(fileInfo.getFilePath())) {
                    if ("vue2".equals(config.getOrmType())) {
                        fileName = String.format("%s/%s",
                                fileInfo.getFilePath().replace("_className_", velocityConfig.getLowClassName()),
                                fileInfo.getFileName()
                                        .replace(".vm", "")
                                        .replace("_className_", velocityConfig.getLowClassName())
                        );
                    } else if ("vue3".equals(config.getOrmType())) {
                        fileName = String.format("%s/%s",
                                fileInfo.getFilePath().replace("_className_", velocityConfig.getLowClassName()),
                                fileInfo.getFileName()
                                        .replace(".vm", "")
                                        .replace("_className_", velocityConfig.getLowClassName())
                        );
                    } else {
                        fileName = String.format("%s/%s/%s",
                                config.getBasePackage().replace(".", "/"),
                                fileInfo.getFilePath(),
                                fileInfo.getFileName()
                                        .replace(".vm", "")
                                        .replace("_className_", velocityConfig.getUpperClassName())
                        );
                    }
                } else {
                    fileName = String.format("%s/%s",
                            config.getBasePackage().replace(".", "/"),
                            fileInfo.getFileName()
                                    .replace(".vm", "")
                                    .replace("_className_", velocityConfig.getUpperClassName())
                    );
                }
                if (results.stream().anyMatch(item -> item.getFileName().equalsIgnoreCase(fileName))) {
                    return;
                }
                String content = VelocityParserPlus.execute(velocityConfig);
                int index = fileName.lastIndexOf(".");
                String fileType = index != -1 ? fileName.substring(index + 1) : "java";
                results.add(new ParserResult(fileName, fileType, content));
            }
        });
        return results;
    }

    /**
     * 解析为Velocity配置类
     *
     * @param config
     * @return
     */
    private static List<VelocityConfig> preParser(JavaCodeConfig config) {
        List<ParserResult> parserResults;
        switch (Objects.requireNonNull(ParserEnum.getByValue(config.getParserType()))) {
            case MYSQL:
            case POSTGRESQL:
            case PLSQL:
                parserResults = customParser(config.getInput(), config.getParserType());
                break;
            default:
                parserResults = CommonSqlParserUtil.parser(config.getInput(), config.getFields());
        }
        if (CollUtil.isEmpty(parserResults)) {
            log.error("解析出错, Input：\n{}", config.getInput());
            return ListUtil.empty();
        }
        return parserResults.stream().map(item -> {
            if (item.isErrored()) {
                return new VelocityConfig()
                        .setTableName(item.getFileName())
                        .setErrorMsg(item.getContent())
                        .setErrored(true);
            }
            String upperClassName = item.getUpperClassName();
            String lowClassName = item.getLowClassName();

            String tableNamePrefix = config.getTableNamePrefix();
            if (tableNamePrefix.endsWith("_")) {
                tableNamePrefix = tableNamePrefix.substring(0, tableNamePrefix.length() - 1);
            }
            //裁剪表名前缀
            if (StrUtil.isNotBlank(tableNamePrefix)) {
                if (upperClassName.startsWith(tableNamePrefix.toUpperCase())) {
                    upperClassName = upperClassName.substring(tableNamePrefix.length());
                    //裁切后若首字母是“_”，则删除
                    if (upperClassName.startsWith("_")) {
                        upperClassName = upperClassName.substring(1);
                    }
                    //裁切后首字母可能是大写，需转为小写
                    lowClassName = VelocityParserPlus.getLowerName(lowClassName.substring(tableNamePrefix.length()));
                }
            }
            String apiPrefix = "/api/";
            if (StrUtil.isNotBlank(config.getApiPathPrefix())) {
                apiPrefix = config.getApiPathPrefix();
                if (!apiPrefix.startsWith("/")) {
                    apiPrefix = "/" + apiPrefix;
                }
                if (!apiPrefix.endsWith("/")) {
                    apiPrefix = apiPrefix + "/";
                }
            }
            return new VelocityConfig()
                    .setMethods(config.getMethods())
                    .setVueMethods(config.getVueMethods())
                    .setComment(StrUtil.isBlank(item.getComment()) ? " " : item.getComment())
                    .setUpperClassName(upperClassName)
                    .setLowClassName(lowClassName)
                    .setTableName(item.getTableName())
                    .setColumns(item.getFields())
                    .setApiPath(apiPrefix + lowClassName);
        }).collect(Collectors.toList());
    }

    /**
     * 数据库表字段元数据转为解析结果实体类
     *
     * @param input
     * @param parserType
     * @return
     */
    private static List<ParserResult> customParser(String input, String parserType) {
        List<DbTableDefinition> dbTableDefinitions =
                SqlParserLoader.SQL_PARSER.get(
                        Objects.requireNonNull(ParserEnum.getByValue(parserType)).getValue()
                ).parser(input);
        if (dbTableDefinitions == null) {
            return null;
        }
        List<ParserResult> result = new ArrayList<>();
        for (DbTableDefinition tableDefinition : dbTableDefinitions) {
            String lowClassName = StrUtil.toCamelCase(tableDefinition.getTableName());
            String upperClassName = VelocityParserPlus.getUpperName(lowClassName);
            result.add(new ParserResult()
                    .setUpperClassName(upperClassName)
                    .setLowClassName(lowClassName)
                    .setComment(StrUtil.isNotBlank(tableDefinition.getTableComment()) ? tableDefinition.getTableComment() : " ")
                    .setTableName(tableDefinition.getTableName())
                    .setFields(tableDefinition.getColumns()
                            .stream()
                            .map(column -> new JavaDefinition.Field()
                                    .setFieldName(StrUtil.toCamelCase(column.getColumnName()))
                                    .setUpperFieldName(VelocityParserPlus.getUpperName(StrUtil.toCamelCase(column.getColumnName())))
                                    .setFieldType(SqlUtil.parser2JavaType(column.getColumnType()))
                                    .setDbName(column.getColumnName())
                                    .setDbType(column.getColumnType())
                                    .setJdbcType(SqlUtil.convertToJdbcType(column.getColumnType()))
                                    .setComment(StrUtil.isNotBlank(column.getColumnComment()) ? column.getColumnComment() : " ")
                                    .setPrimaryKey(column.isPrimaryKey())
                            )
                            .collect(Collectors.toList()))
            );
        }
        return result;
    }

    private static void buildFileTree(TreeVO tree, String[] dirArr, int index) {
        if (index < dirArr.length) {
            String key = dirArr[index];
            TreeVO child = new TreeVO().setKey(key).setTitle(dirArr[index]);
            if (tree.getChildren() == null) {
                tree.addChildren(child);
            } else {
                List<TreeVO> existsChild = tree.getChildren().stream().filter(item -> item.getTitle().equals(dirArr[index])).collect(Collectors.toList());
                if (existsChild.size() > 0) {
                    child = existsChild.get(0);
                } else {
                    tree.addChildren(child);
                }
            }
            buildFileTree(child, dirArr, index + 1);
        }
        tree.setLeaf(tree.getChildren() == null);
    }
}
