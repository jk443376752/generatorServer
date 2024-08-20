package com.tool4j.generator.util.parser.sql.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.tool4j.generator.entity.SqlTable;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class SqlUtil {

    /**
     * 获取sql字段注释
     *
     * @param columnSpecs
     * @return
     */
    public static String getColumnComment(List<String> columnSpecs) {
        if (CollUtil.isNotEmpty(columnSpecs)) {
            for (int i = 0; i < columnSpecs.size(); i++) {
                if (JSqlVisitor.COMMENT.equalsIgnoreCase(columnSpecs.get(i)) && i < columnSpecs.size() - 1) {
                    return columnSpecs.get(i + 1).replaceAll("'", "");
                }
            }
        }
        return " ";
    }

    /**
     * 解析sql语句
     *
     * @param createTableSql
     * @return {@link SqlTable}
     */
    public static SqlTable parserSqlTable(String createTableSql, List<String> fields) {
        try {
            createTableSql = createTableSql.replace("`", "");
            CCJSqlParserManager mgr = new CCJSqlParserManager();
            Statement stmt = mgr.parse(new StringReader(createTableSql));
            JSqlVisitor sqlVisitor = JSqlVisitor.newInstance();
            stmt.accept(sqlVisitor);
            SqlTable sqlTable = sqlVisitor.getSqlTable();
            if (CollUtil.isNotEmpty(fields)) {
                sqlTable.setColumnDefinitions(sqlTable.getColumnDefinitions()
                        .stream().filter(columnDefinition -> fields.contains(
                                StrUtil.toCamelCase(columnDefinition.getColumnName()))).collect(Collectors.toList()));
            }
            return sqlTable;
        } catch (Exception e) {
            log.error("解析sql失败：{}", e);
        }
        return null;
    }

    public static String parser2JavaType(String dbType) {
        if (dbType == null) {
            return "String";
        }
        switch (dbType.toUpperCase()) {
            case "INT":
            case "INT4":
            case "INT8":
            case "INT16":
            case "TINYINT":
            case "SMALLINT":
            case "MEDIUMINT":
            case "INTEGER":
                return "Integer";
            case "BIGINT":
                return "Long";
            case "FLOAT":
            case "DOUBLE":
            case "DECIMAL":
            case "NUMBER":
                return "Float";
            case "boolean":
                return "Boolean";
            case "ENUM":
            case "SET":
                return "List";
            case "DATE":
            case "DATETIME":
            case "TIMESTAMP":
                return "Date";
            default:
                return "String";
        }
    }

    /**
     * 将数据库字段类型转为JdbcType
     *
     * @param columnType
     * @return
     */
    public static String convertToJdbcType(String columnType) {
        String javaType = SqlUtil.parser2JavaType(columnType);
        switch (javaType.toUpperCase()) {
            case "INTEGER":
                return "INTEGER";
            case "LONG":
                return "BIGINT";
            case "FLOAT":
                return "FLOAT";
            case "BOOLEAN":
                return "BOOLEAN";
            case "LIST":
                return "ARRAY";
            case "DATE":
                return "DATE";
            case "STRING":
            default:
                return "VARCHAR";
        }
    }

}
