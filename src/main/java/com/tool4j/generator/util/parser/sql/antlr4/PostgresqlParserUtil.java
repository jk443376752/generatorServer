package com.tool4j.generator.util.parser.sql.antlr4;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.tool4j.generator.util.parser.sql.DbTableDefinition;
import com.tool4j.generator.util.parser.sql.antlr4.postgresql.PostgreSQLLexer;
import com.tool4j.generator.util.parser.sql.antlr4.postgresql.PostgreSQLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Mysql语法解析器
 *
 * @author Deng.Weiping
 * @since 2023/11/20 9:35
 */
public class PostgresqlParserUtil implements SqlParser {

    @Override
    public List<DbTableDefinition> parser(String input) {
        //词法分析器
        PostgreSQLLexer postgreSQLLexer = new PostgreSQLLexer(CharStreams.fromString(input.toUpperCase()));
        //词法符号的缓冲区,用于存储词法分析器生成的词法符号
        CommonTokenStream commonTokenStream = new CommonTokenStream(postgreSQLLexer);
        //新建一个语法分析器，处理词法符号缓冲区内容
        PostgreSQLParser postgreSQLParser = new PostgreSQLParser(commonTokenStream);
        ParseTree tree = postgreSQLParser.stmtblock();
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
        List<ParseTree> commentNodes = new ArrayList<>();
        filterCreateTableNodes(root, createTableNodes, commentNodes);
        List<DbTableDefinition> result = new ArrayList<>();
        for (ParseTree createTableNode : createTableNodes) {
            DbTableDefinition tableDefinition = new DbTableDefinition();
            recursive(createTableNode, tableDefinition);
            result.add(tableDefinition);
        }
        if (CollUtil.isNotEmpty(result) && CollUtil.isNotEmpty(commentNodes)) {
            for (ParseTree commentNode : commentNodes) {
                parserComment(result, commentNode);
            }
        }
        return result;
    }

    private static void parserComment(List<DbTableDefinition> list, ParseTree commentNode) {
        ParserRuleContext prc = (ParserRuleContext) commentNode;
        if (prc.children != null) {
            String tableName = null;
            String columnName = null;
            String comment = null;
            for (ParseTree child : prc.children) {
                if (child instanceof PostgreSQLParser.Any_nameContext) {
                    if (child.getText().contains(".")) {
                        String[] split = child.getText().split("\\.");
                        tableName = split[split.length - 2]
                                .toLowerCase()
                                .replace("\"", "")
                                .replace("'", "")
                                .replace("`", "");
                        columnName = split[split.length - 1].toLowerCase()
                                .replace("\"", "")
                                .replace("'", "")
                                .replace("`", "");
                    } else {
                        columnName = child.getText().toLowerCase()
                                .replace("\"", "")
                                .replace("'", "")
                                .replace("`", "");
                    }
                } else if (child instanceof PostgreSQLParser.Comment_textContext) {
                    comment = child.getText()
                            .replace("\"", "")
                            .replace("'", "")
                            .replace("`", "");
                }
            }

            for (DbTableDefinition dbTableDefinition : list) {
                if (dbTableDefinition.getTableName().equals(columnName)) {
                    dbTableDefinition.setTableComment(comment);
                    return;
                } else if (dbTableDefinition.getTableName().equals(tableName)) {
                    for (DbTableDefinition.Column column : dbTableDefinition.getColumns()) {
                        if (column.getColumnName().equals(columnName)) {
                            column.setColumnComment(comment);
                            return;
                        }
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
    private static void filterCreateTableNodes(ParseTree root, List<ParseTree> createTableNodes, List<ParseTree> commentNodes) {
        if (root instanceof PostgreSQLParser.CreatestmtContext) {
            createTableNodes.add(root);
        } else if (root instanceof PostgreSQLParser.CommentstmtContext) {
            commentNodes.add(root);
        } else {
            if (root instanceof ParserRuleContext) {
                ParserRuleContext prc = (ParserRuleContext) root;
                if (prc.children != null) {
                    for (ParseTree child : prc.children) {
                        filterCreateTableNodes(child, createTableNodes, commentNodes);
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
        if (root instanceof PostgreSQLParser.Qualified_nameContext) {
            //表
            parserTableName(root, tableDefinition);
        } else if (root instanceof PostgreSQLParser.OpttableelementlistContext) {
            //字段
            if (root instanceof ParserRuleContext) {
                ParserRuleContext prc = (ParserRuleContext) root;
                if (prc.children != null) {
                    for (ParseTree child : prc.children) {
                        parserTableElements(child, tableDefinition);
                    }
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
     * 解析TableName节点
     *
     * @param tableNameNode
     * @param tableDefinition
     */
    private static void parserTableName(ParseTree tableNameNode, DbTableDefinition tableDefinition) {
        String[] split = tableNameNode.getText().split("\\.");
        tableDefinition.setTableName(split[split.length - 1]
                .replace("\"", "")
                .replace("'", "")
                .replace("`", "")
                .toLowerCase());
    }

    /**
     * 解析TableOption节点
     *
     * @param tableOptionNode
     * @param tableDefinition
     */
    /*private static void parserTableOption(ParseTree tableOptionNode, DbTableDefinition tableDefinition) {
        ParserRuleContext prc = (ParserRuleContext) tableOptionNode;
        if ((tableOptionNode instanceof PostgreSQLParser.TableOptionCommentContext)) {
            if (prc.children != null) {
                if (prc.children != null) {
                    for (int i = 0; i < prc.children.size(); i++) {
                        if ("COMMENT".equalsIgnoreCase(prc.getChild(i).getText())
                                && (i + 1) < prc.children.size()) {
                            tableDefinition.setTableComment(prc.getChild(i + 1).getText());
                            break;
                        }
                    }
                }
            }
        } else {
            if (prc.children != null) {
                for (ParseTree child : prc.children) {
                    parserTableName(child, tableDefinition);
                }
            }
        }
    }*/

    /**
     * 解析TableElements（表字段）节点
     *
     * @param createDefinitionsNode
     * @param tableDefinition
     */
    private static void parserTableElements(ParseTree createDefinitionsNode, DbTableDefinition tableDefinition) {
        if (createDefinitionsNode instanceof ParserRuleContext) {
            ParserRuleContext prc = (ParserRuleContext) createDefinitionsNode;
            if (prc.children != null) {
                List<DbTableDefinition.Column> columns = new ArrayList<>();
                for (ParseTree child : prc.children) {
                    if (child instanceof PostgreSQLParser.TableelementContext) {
                        DbTableDefinition.Column column = new DbTableDefinition.Column();
                        parserColumn(child, column);
                        if (StrUtil.isNotBlank(column.getColumnName())) {
                            columns.add(column);
                        }
                    }
                }
                tableDefinition.setColumns(columns);
            }
        }
    }

    private static void parserColumn(ParseTree columnNode, DbTableDefinition.Column column) {
        if (columnNode instanceof ParserRuleContext) {
            if (columnNode instanceof PostgreSQLParser.ColumnDefContext) {
                PostgreSQLParser.ColumnDefContext prc = (PostgreSQLParser.ColumnDefContext) columnNode;
                if (prc.children != null) {
                    for (ParseTree child : prc.children) {
                        if (child instanceof PostgreSQLParser.ColidContext) {
                            column.setColumnName(child.getText()
                                    .replace("\"", "")
                                    .replace("'", "")
                                    .replace("`", "")
                                    .toLowerCase());
                        } else if (child instanceof PostgreSQLParser.TypenameContext) {
                            parserColumnType(child, column);
                        }
                    }
                }
            } else {
                ParserRuleContext prc = (ParserRuleContext) columnNode;
                if (prc.children != null) {
                    for (ParseTree child : prc.children) {
                        parserColumn(child, column);
                    }
                }
            }
        }
    }

    private static void parserColumnType(ParseTree columnNode, DbTableDefinition.Column column) {
        PostgreSQLParser.TypenameContext typenameContext = (PostgreSQLParser.TypenameContext) columnNode;
        if (typenameContext.children != null) {
            for (ParseTree item : typenameContext.children) {
                if (item instanceof PostgreSQLParser.SimpletypenameContext) {
                    PostgreSQLParser.SimpletypenameContext type = (PostgreSQLParser.SimpletypenameContext) item;
                    if (type.children != null) {
                        for (ParseTree child : type.children) {
                            ParserRuleContext prc = (ParserRuleContext) child;
                            if (prc.children != null) {
                                column.setColumnType(prc.children.get(0).getText().toLowerCase());
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
}
