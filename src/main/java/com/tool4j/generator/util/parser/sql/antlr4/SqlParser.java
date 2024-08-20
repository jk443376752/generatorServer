package com.tool4j.generator.util.parser.sql.antlr4;


import com.tool4j.generator.util.parser.sql.DbTableDefinition;

import java.util.List;

/**
 * SQL 解析器
 *
 * @since 0.0.1
 */
public interface SqlParser {

    List<DbTableDefinition> parser(String input);

}
