package com.tool4j.generator.util.parser;

import com.tool4j.generator.entity.vo.ParseDefinition;
import com.tool4j.generator.entity.vo.ParserResult;

/**
 * 文本解析器接口
 *
 * @since 0.0.1
 */
public interface TextParser {

    ParserResult execute(ParseDefinition parseDefinition);
}
