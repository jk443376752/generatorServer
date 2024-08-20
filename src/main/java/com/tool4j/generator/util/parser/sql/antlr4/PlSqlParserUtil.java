package com.tool4j.generator.util.parser.sql.antlr4;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.tool4j.generator.util.parser.sql.DbTableDefinition;
import com.tool4j.generator.util.parser.sql.antlr4.plsql.PlSqlLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import com.tool4j.generator.util.parser.sql.antlr4.plsql.PlSqlParser;

import java.util.ArrayList;
import java.util.List;

/**
 * PlSql语法解析器
 *
 * @author Deng.Weiping
 * @since 2023/11/20 9:35
 */
public class PlSqlParserUtil implements SqlParser {

    @Override
    public List<DbTableDefinition> parser(String input) {
        //词法分析器
        PlSqlLexer plSqlLexer = new PlSqlLexer(CharStreams.fromString(input.toUpperCase()));
        //词法符号的缓冲区,用于存储词法分析器生成的词法符号
        CommonTokenStream commonTokenStream = new CommonTokenStream(plSqlLexer);
        //新建一个语法分析器，处理词法符号缓冲区内容
        PlSqlParser plSqlParser = new PlSqlParser(commonTokenStream);
        ParseTree tree = plSqlParser.sql_script();
        return parserTree(tree);
    }

    /**
     * 解析sql语法树
     *
     * @param root
     * @return
     */
    public static List<DbTableDefinition> parserTree(ParseTree root) {
        List<ParseTree> createTableNodes = new ArrayList<>();
        List<ParseTree> columnCommentNodes = new ArrayList<>();
        List<ParseTree> tableCommentNodes = new ArrayList<>();
        filterCreateTableNodes(root, createTableNodes, tableCommentNodes, columnCommentNodes);
        List<DbTableDefinition> result = new ArrayList<>();
        for (ParseTree createTableNode : createTableNodes) {
            DbTableDefinition tableDefinition = new DbTableDefinition();
            recursive(createTableNode, tableDefinition);
            result.add(tableDefinition);
        }
        //解析表注释
        if (CollUtil.isNotEmpty(result) && CollUtil.isNotEmpty(tableCommentNodes)) {
            parserTableComment(result, tableCommentNodes);
        }
        // 字段注释
        if (CollUtil.isNotEmpty(result) && CollUtil.isNotEmpty(columnCommentNodes)) {
            parserColumnComment(result, columnCommentNodes);
        }
        return result;
    }

    /**
     * 解析表注释
     *
     * @param list
     * @param tableCommentNodes
     */
    private static void parserTableComment(List<DbTableDefinition> list, List<ParseTree> tableCommentNodes) {
        for (ParseTree tableCommentNode : tableCommentNodes) {
            ParserRuleContext prc = (ParserRuleContext) tableCommentNode;
            String tableName = null;
            String comment = null;
            if (prc.children != null) {
                for (ParseTree child : prc.children) {
                    if (child instanceof PlSqlParser.Tableview_nameContext) {
                        tableName = child.getText().toLowerCase()
                                .replace("\"", "")
                                .replace("'", "")
                                .replace("`", "");
                    }
                    if (child instanceof PlSqlParser.Quoted_stringContext) {
                        comment = child.getText()
                                .replace("\"", "")
                                .replace("'", "")
                                .replace("`", "");
                    }
                }
            }
            if (tableName != null && comment != null) {
                for (DbTableDefinition dbTableDefinition : list) {
                    if (dbTableDefinition.getTableName().equals(tableName)) {
                        dbTableDefinition.setTableComment(comment);
                        break;
                    }
                }
            }
        }
    }

    public static void parserColumnComment(List<DbTableDefinition> list, List<ParseTree> columnNodes) {
        for (ParseTree columnNode : columnNodes) {
            ParserRuleContext prc = (ParserRuleContext) columnNode;
            String tableName = null;
            String columnName = null;
            String comment = null;
            if (prc.children != null) {
                for (ParseTree child : prc.children) {
                    if (child instanceof PlSqlParser.Column_nameContext) {
                        String[] split = child.getText().split("\\.");
                        if (split.length < 2) {
                            break;
                        }
                        tableName = split[split.length - 2].toLowerCase()
                                .replace("\"", "")
                                .replace("'", "")
                                .replace("`", "");
                        columnName = split[split.length - 1].toLowerCase()
                                .replace("\"", "")
                                .replace("'", "")
                                .replace("`", "");
                    } else if (child instanceof PlSqlParser.Quoted_stringContext) {
                        comment = child.getText()
                                .replace("\"", "")
                                .replace("'", "")
                                .replace("`", "");
                    }
                }
            }
            if (tableName != null && columnName != null && comment != null) {
                for (DbTableDefinition dbTableDefinition : list) {
                    if (dbTableDefinition.getTableName().equals(tableName)) {
                        for (DbTableDefinition.Column column : dbTableDefinition.getColumns()) {
                            if (column.getColumnName().equals(columnName)) {
                                column.setColumnComment(comment);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 过滤非建表节点
     *
     * @param root
     * @param createTableNodes
     */
    private static void filterCreateTableNodes(ParseTree root, List<ParseTree> createTableNodes, List<ParseTree> tableCommentNodes, List<ParseTree> columnCommentNodes) {
        if (root instanceof PlSqlParser.Create_tableContext) {
            createTableNodes.add(root);
        } else if (root instanceof PlSqlParser.Comment_on_columnContext) {
            columnCommentNodes.add(root);
        } else if (root instanceof PlSqlParser.Comment_on_tableContext) {
            tableCommentNodes.add(root);
        } else {
            if (root instanceof ParserRuleContext) {
                ParserRuleContext prc = (ParserRuleContext) root;
                if (prc.children != null) {
                    for (ParseTree child : prc.children) {
                        filterCreateTableNodes(child, createTableNodes, columnCommentNodes, tableCommentNodes);
                    }
                }
            }
        }
    }

    /**
     * 递归解析树节点
     *
     * @param root
     * @param tableDefinition
     */
    private static void recursive(ParseTree root, DbTableDefinition tableDefinition) {
        //TODO 表注释
        if (root instanceof PlSqlParser.Table_nameContext) {
            if (root instanceof ParserRuleContext) {
                ParserRuleContext prc = (ParserRuleContext) root;
                if (prc.children != null) {
                    for (ParseTree child : prc.children) {
                        if (child instanceof PlSqlParser.IdentifierContext) {
                            tableDefinition.setTableName(child.getText()
                                    .replace("\"", "")
                                    .replace("'", "")
                                    .replace("`", "")
                                    .toLowerCase());
                        }
                    }
                }
            }
            return;
        } else if (root instanceof PlSqlParser.Relational_tableContext) {
            if (root instanceof ParserRuleContext) {
                ParserRuleContext prc = (ParserRuleContext) root;
                if (prc.children != null) {
                    List<DbTableDefinition.Column> columns = new ArrayList<>();
                    for (ParseTree child : prc.children) {
                        if (child instanceof PlSqlParser.Relational_propertyContext) {
                            DbTableDefinition.Column column = new DbTableDefinition.Column();
                            parserColumns(child, column);
                            if (StrUtil.isNotBlank(column.getColumnName())) {
                                columns.add(column);
                            }
                        }
                    }
                    tableDefinition.setColumns(columns);
                }
            }
            return;
        } else {
            if (root instanceof ParserRuleContext) {
                ParserRuleContext prc = (ParserRuleContext) root;
                if (prc.children != null) {
                    for (ParseTree child : prc.children) {
                        recursive(child, tableDefinition);
                    }
                }
            }
        }
    }

    /**
     * 解析字段定义节点
     *
     * @param columnNode
     * @param column
     */
    private static void parserColumns(ParseTree columnNode, DbTableDefinition.Column column) {
        if (columnNode instanceof ParserRuleContext) {
            ParserRuleContext prc = (ParserRuleContext) columnNode;
            if (prc.children != null) {
                for (ParseTree child : prc.children) {
                    if (child instanceof PlSqlParser.Column_definitionContext) {
                        ParserRuleContext columnDefinition = (ParserRuleContext) child;
                        if (columnDefinition.children != null) {
                            for (ParseTree definition : columnDefinition.children) {
                                if (definition instanceof PlSqlParser.Column_nameContext) {
                                    column.setColumnName(definition.getText()
                                            .replace("\"", "")
                                            .replace("'", "")
                                            .replace("`", "")
                                            .toLowerCase());
                                } else if (definition instanceof PlSqlParser.DatatypeContext) {
                                    ParserRuleContext dataType = (ParserRuleContext) definition;
                                    if (dataType.children != null) {
                                        for (ParseTree typeItem : dataType.children) {
                                            if (typeItem instanceof PlSqlParser.Native_datatype_elementContext) {
                                                column.setColumnType(typeItem.getText());
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
