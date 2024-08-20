package com.tool4j.generator.util.parser;

import com.tool4j.generator.common.ParserEnum;
import com.tool4j.generator.util.parser.sql.antlr4.MysqlParserUtil;
import com.tool4j.generator.util.parser.sql.antlr4.PlSqlParserUtil;
import com.tool4j.generator.util.parser.sql.antlr4.PostgresqlParserUtil;
import com.tool4j.generator.util.parser.sql.antlr4.SqlParser;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL 解析器加载器
 */
@Slf4j
public class SqlParserLoader {

    public static final String TEXT_TYPE_JAVA = "java";

    public static Map<String, SqlParser> SQL_PARSER = new HashMap() {{
        put(ParserEnum.MYSQL.getValue(), new MysqlParserUtil());
        put(ParserEnum.POSTGRESQL.getValue(), new PostgresqlParserUtil());
        put(ParserEnum.PLSQL.getValue(), new PlSqlParserUtil());
    }};

}
