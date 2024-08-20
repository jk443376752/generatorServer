package com.tool4j.generator.util.parser.sql.common;

import cn.hutool.core.util.StrUtil;
import com.tool4j.generator.entity.vo.ParseDefinition;
import com.tool4j.generator.entity.vo.ParserResult;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用 SQL 解析器
 *
 * @since 0.0.1
 */
@Slf4j
public class CommonSqlParserUtil {

    public static List<ParserResult> parser(String input, List<String> fields) {
        List<ParserResult> result = new ArrayList<>();
        String regex = "(?i)create\\s+table.*?(?=create\\s+table|$)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        // 遍历匹配结果
        while (matcher.find()) {
            String item = matcher.group(0);
            if (StrUtil.isBlank(item)) {
                continue;
            }
            item = replaceCommentSemicolon(item);
            if (item.contains(";")) {
                for (String s : item.split(";")) {
                    if (StrUtil.isBlank(item)) {
                        continue;
                    }
                    ParserResult parserItem = parserItem(s, fields);
                    if (parserItem != null) {
                        parserItem.setInput(item);
                        result.add(parserItem);
                    }
                }
            } else {
                ParserResult parserItem = parserItem(item, fields);
                if (parserItem != null) {
                    parserItem.setInput(item);
                    result.add(parserItem);
                }
            }
        }

        for (int i = 0; i < result.size(); i++) {
            if (StrUtil.isBlank(result.get(i).getFileName())) {
                String fileName = "result" + (i + 1);
                result.get(i).setFileName(fileName);
            }
        }
        return result;
    }

    private static ParserResult parserItem(String item, List<String> fields) {
        String crtRegex = "(?i)create\\s+table\\s+(.*?)\\)(.*?)";
        Pattern crtPattern = Pattern.compile(crtRegex, Pattern.DOTALL);
        Matcher crtMatcher = crtPattern.matcher(item);
        if (!crtMatcher.matches()) {
            return null;
        }

        try {
            return parseSql(new ParseDefinition().setInput(item).setFields(fields));
        } catch (JSQLParserException e) {
            log.error("SQL解析失败：sql={}", item, e);
            return new ParserResult()
                    .setInput(item)
                    .setErrored(true)
                    .setContent(String.format("SQL解析失败，建议您切换SQL解析器重试 \n\nsql语句：\n%s \n\n错误信息：\n%s", item, e.getCause().getMessage()));
        }
    }

    public static ParserResult parseSql(ParseDefinition definition) throws JSQLParserException {
        try {
            //JSqlParser不支持字符集设置语句：CHARACTER = xxx 或 CHARACTER SET xxx
            String characterRegex = "(?i)CHARACTER\\s+SET\\s*=\\s*\\w+";
            String characterRegex2 = "(?i)CHARACTER\\s+SET\\s+\\w+\\s+";
            String input = definition.getInput();
            input = input.replaceAll(characterRegex, " ").replaceAll(characterRegex2, " ");
            definition.setInput(input);
            CCJSqlParserManager mgr = new CCJSqlParserManager();
            Statement stmt = mgr.parse(new StringReader(definition.getInput()));
            JSqlVisitor sqlVisitor = JSqlVisitor.newInstance();
            stmt.accept(sqlVisitor);
            definition.setSourceType(sqlVisitor.getSqlType());
            return sqlVisitor.execute(definition);
        } catch (Exception e) {
            throw new JSQLParserException("解析SQL失败：", e);
        }
    }

    /**
     * 替换注释中的分号，避免解析误判
     *
     * @param sql
     * @return
     */
    public static String replaceCommentSemicolon(String sql) {
        // 正则表达式匹配单引号或双引号之间的内容
        String singleQuotePattern = "('([^']|'')*')";
        String doubleQuotePattern = "\"[^\"]*\"";
        String pattern = String.format("%s|%s", singleQuotePattern, doubleQuotePattern);

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(sql);

        String result = sql;
        // 查找匹配的内容并判断分号位置
        while (matcher.find()) {
            String match = matcher.group();
            // 判断分号位置
            if (match.contains(";")) {
                result = result.replace(match, match.replace(";", "；"));
            }
        }
        return result;
    }
}
