package com.tool4j.generator.common;

import lombok.Getter;

/**
 * 解析器枚举类
 *
 * @since 0.0.1
 */
@Getter
public enum ParserEnum {

    COMMON("common"),
    MYSQL("mysql"),
    POSTGRESQL("postgresql"),
    PLSQL("plsql"),
    ;

    private String value;

    ParserEnum(String value) {
        this.value = value;
    }

    public static ParserEnum getByValue(String value) {
        for (ParserEnum parserEnum : ParserEnum.values()) {
            if (parserEnum.value.equals(value)) {
                return parserEnum;
            }
        }
        return null;
    }
}
