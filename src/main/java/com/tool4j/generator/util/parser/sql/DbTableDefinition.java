package com.tool4j.generator.util.parser.sql;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 数据库表定义
 *
 * @author Deng.Weiping
 * @since 2023/11/20 10:29
 */
@Data
@Accessors(chain = true)
public class DbTableDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表注释
     */
    private String tableComment;

    /**
     * 字段集合
     */
    private List<Column> columns;

    @Data
    @Accessors(chain = true)
    public static class Column {

        /**
         * 字段名称
         */
        private String columnName;

        /**
         * 字段类型
         */
        private String columnType;

        /**
         * 是否主键
         */
        private boolean primaryKey;

        /**
         * 字段注释
         */
        private String columnComment;

    }
}
