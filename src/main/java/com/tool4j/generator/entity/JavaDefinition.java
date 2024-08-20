package com.tool4j.generator.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * java实体类定义信息
 *
 * @author Deng.Weiping
 * @since 2023/8/1 9:43
 */
@Data
@Accessors(chain = true)
public class JavaDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类名称
     */
    private String clazzName;

    /**
     * 数据库表名称
     */
    private String tableName;

    /**
     * 类注释
     */
    private String comment;

    /**
     * 实体类字段集合
     */
    private List<Field> fields;

    @Data
    @Accessors(chain = true)
    public static class Field {

        /**
         * 字段名称
         */
        private String fieldName;

        /**
         * 字段名称首字母大写
         */
        private String upperFieldName;

        /**
         * 字段类型
         */
        private String fieldType;

        /**
         * 数据库字段名称
         */
        private String dbName;

        /**
         * 数据库字段类型
         */
        private String dbType;

        /**
         * jdbcType
         */
        private String jdbcType;

        /**
         * 是否是数据库字段
         */
        private Boolean existsTableField;

        /**
         * 是否主键
         */
        private Boolean primaryKey;

        /**
         * 字段注释
         */
        private String comment;

    }
}
