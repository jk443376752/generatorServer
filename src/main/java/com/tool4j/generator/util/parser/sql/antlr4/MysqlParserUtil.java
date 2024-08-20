package com.tool4j.generator.util.parser.sql.antlr4;

import cn.hutool.core.util.StrUtil;
import com.tool4j.generator.util.parser.sql.DbTableDefinition;
import com.tool4j.generator.util.parser.sql.antlr4.mysql.MySqlLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import com.tool4j.generator.util.parser.sql.antlr4.mysql.MySqlParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Mysql语法解析器
 *
 * @author Deng.Weiping
 * @since 2023/11/20 9:35
 */
public class MysqlParserUtil implements SqlParser {

    @Override
    public List<DbTableDefinition> parser(String input) {
        //词法分析器
        MySqlLexer mySqlLexer = new MySqlLexer(CharStreams.fromString(input.toUpperCase()));
        //词法符号的缓冲区,用于存储词法分析器生成的词法符号
        CommonTokenStream commonTokenStream = new CommonTokenStream(mySqlLexer);
        //新建一个语法分析器，处理词法符号缓冲区内容
        MySqlParser mySqlParser = new MySqlParser(commonTokenStream);
        ParseTree tree = mySqlParser.sqlStatements();
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
        List<ParseTree> alterTableNodes = new ArrayList<>();
        filterCreateTableNodes(root, createTableNodes, alterTableNodes);
        List<DbTableDefinition> result = new ArrayList<>();
        for (ParseTree createTableNode : createTableNodes) {
            DbTableDefinition tableDefinition = new DbTableDefinition();
            recursive(createTableNode, tableDefinition);
            result.add(tableDefinition);
        }
        postTableDefinition(result, alterTableNodes);
        return result;
    }

    /**
     * 后置处理表字段变更
     * 表字段注释变更 已处理
     * TODO 字段名变更
     * TODO 字段类型变更
     *
     * @param list
     * @param alterTableNodes
     */
    private static void postTableDefinition(List<DbTableDefinition> list, List<ParseTree> alterTableNodes) {
        for (ParseTree alterTableNode : alterTableNodes) {
            ParserRuleContext prc = (ParserRuleContext) alterTableNode;
            String tableName = null;
            String tableComment = null;
            String columnName = null;
            String columnComment = null;
            String dbType = null;
            String oldColumn = null;
            String newColumn = null;
            if (prc.children != null) {
                for (ParseTree child : prc.children) {
                    if (child instanceof MySqlParser.TableNameContext) {
                        tableName = child.getText().toLowerCase()
                                .replace("\"", "")
                                .replace("'", "")
                                .replace("`", "");
                    } else if (child instanceof MySqlParser.AlterByTableOptionContext) {
                        ParserRuleContext alterByTableOption = (ParserRuleContext) child;
                        if (alterByTableOption.children != null) {
                            for (ParseTree tableOptionChild : alterByTableOption.children) {
                                if (tableOptionChild instanceof MySqlParser.TableOptionCommentContext) {
                                    ParserRuleContext tableOptionComment = (ParserRuleContext) tableOptionChild;
                                    if (tableOptionComment.children != null) {
                                        tableComment = tableOptionComment.getChild(tableOptionComment.children.size() - 1).getText()
                                                .replace("\"", "")
                                                .replace("'", "")
                                                .replace("`", "");
                                    }
                                }
                            }
                        }
                    } else if (child instanceof MySqlParser.AlterByModifyColumnContext) {
                        ParserRuleContext alterByColumnOption = (ParserRuleContext) child;
                        if (alterByColumnOption.children != null) {
                            for (ParseTree columnOption : alterByColumnOption.children) {
                                if (columnOption instanceof MySqlParser.UidContext) {
                                    columnName = columnOption.getText().toLowerCase()
                                            .replace("\"", "")
                                            .replace("'", "")
                                            .replace("`", "");
                                } else if (columnOption instanceof MySqlParser.ColumnDefinitionContext) {
                                    ParserRuleContext columnOptionChild = (ParserRuleContext) columnOption;
                                    if (columnOptionChild.children != null) {
                                        for (ParseTree columnOptionChildChild : columnOptionChild.children) {
                                            if (columnOptionChildChild instanceof MySqlParser.CommentColumnConstraintContext) {
                                                MySqlParser.CommentColumnConstraintContext columnConstraintContext = ((MySqlParser.CommentColumnConstraintContext) columnOptionChildChild);
                                                columnComment = columnConstraintContext.children.get(columnConstraintContext.children.size() - 1).getText()
                                                        .replace("\"", "")
                                                        .replace("'", "")
                                                        .replace("`", "");
                                                break;
                                            } else if (columnOptionChildChild instanceof MySqlParser.DimensionDataTypeContext) {
                                                dbType = ((MySqlParser.DimensionDataTypeContext) columnOptionChildChild).typeName.getText();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (child instanceof MySqlParser.AlterByChangeColumnContext) {
                        MySqlParser.AlterByChangeColumnContext alterByChangeColumn = (MySqlParser.AlterByChangeColumnContext) child;
                        oldColumn = alterByChangeColumn.oldColumn.getText().toLowerCase()
                                .replace("\"", "")
                                .replace("'", "")
                                .replace("`", "");
                        newColumn = alterByChangeColumn.newColumn.getText().toLowerCase()
                                .replace("\"", "")
                                .replace("'", "")
                                .replace("`", "");
                        if (alterByChangeColumn.children != null) {
                            for (ParseTree alterByChangeColumnChild : alterByChangeColumn.children) {
                                if (alterByChangeColumnChild instanceof MySqlParser.ColumnDefinitionContext) {
                                    ParserRuleContext columnOptionChild = (ParserRuleContext) alterByChangeColumnChild;
                                    if (columnOptionChild.children != null) {
                                        for (ParseTree columnOptionChildChild : columnOptionChild.children) {
                                            if (columnOptionChildChild instanceof MySqlParser.CommentColumnConstraintContext) {
                                                MySqlParser.CommentColumnConstraintContext columnConstraintContext = ((MySqlParser.CommentColumnConstraintContext) columnOptionChildChild);
                                                columnComment = columnConstraintContext.children.get(columnConstraintContext.children.size() - 1).getText()
                                                        .replace("\"", "")
                                                        .replace("'", "")
                                                        .replace("`", "");
                                                break;
                                            } else if (columnOptionChildChild instanceof MySqlParser.DimensionDataTypeContext) {
                                                dbType = ((MySqlParser.DimensionDataTypeContext) columnOptionChildChild).typeName.getText();
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (tableName != null) {
                if (tableComment != null) {
                    for (DbTableDefinition dbTableDefinition : list) {
                        if (dbTableDefinition.getTableName().equals(tableName)) {
                            dbTableDefinition.setTableComment(tableComment);
                            break;
                        }
                    }
                }
                if (oldColumn != null && newColumn != null) {
                    for (DbTableDefinition dbTableDefinition : list) {
                        if (dbTableDefinition.getTableName().equals(tableName)) {
                            for (DbTableDefinition.Column column : dbTableDefinition.getColumns()) {
                                if (column.getColumnName().equals(oldColumn)) {
                                    column.setColumnName(newColumn);
                                    columnName = newColumn;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                if (columnName != null) {
                    if (columnComment != null) {
                        for (DbTableDefinition dbTableDefinition : list) {
                            if (dbTableDefinition.getTableName().equals(tableName)) {
                                for (DbTableDefinition.Column column : dbTableDefinition.getColumns()) {
                                    if (column.getColumnName().equals(columnName)) {
                                        column.setColumnComment(columnComment);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    if (dbType != null) {
                        for (DbTableDefinition dbTableDefinition : list) {
                            if (dbTableDefinition.getTableName().equals(tableName)) {
                                for (DbTableDefinition.Column column : dbTableDefinition.getColumns()) {
                                    if (column.getColumnName().equals(columnName)) {
                                        column.setColumnType(dbType);
                                        break;
                                    }
                                }
                                break;
                            }
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
    private static void filterCreateTableNodes(ParseTree
                                                       root, List<ParseTree> createTableNodes, List<ParseTree> alterTableNodes) {
        if (root instanceof MySqlParser.ColumnCreateTableContext) {
            createTableNodes.add(root);
        } else if (root instanceof MySqlParser.AlterTableContext) {
            alterTableNodes.add(root);
        } else {
            if (root instanceof ParserRuleContext) {
                ParserRuleContext prc = (ParserRuleContext) root;
                if (prc.children != null) {
                    for (ParseTree child : prc.children) {
                        filterCreateTableNodes(child, createTableNodes, alterTableNodes);
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
        if (root instanceof MySqlParser.ColumnCreateTableContext) {
            if (root instanceof ParserRuleContext) {
                ParserRuleContext prc = (ParserRuleContext) root;
                if (prc.children != null) {
                    for (ParseTree child : prc.children) {
                        if (child instanceof MySqlParser.TableNameContext) {
                            parserTableName(child, tableDefinition);
                        } else if (child instanceof MySqlParser.TableOptionContext) {
                            parserTableOption(child, tableDefinition);
                        } else if (child instanceof MySqlParser.CreateDefinitionsContext) {
                            parserCreateDefinitions(child, tableDefinition);
                        }
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
        if (tableNameNode instanceof ParserRuleContext) {
            if (tableNameNode instanceof MySqlParser.FullIdContext) {
                String[] split = tableNameNode.getText().split("\\.");
                tableDefinition.setTableName(split[split.length - 1]
                        .replace("\"", "")
                        .replace("'", "")
                        .replace("`", "")
                        .toLowerCase());
            } else {
                ParserRuleContext prc = (ParserRuleContext) tableNameNode;
                if (prc.children != null) {
                    for (ParseTree child : prc.children) {
                        parserTableName(child, tableDefinition);
                    }
                }
            }
        }
    }

    /**
     * 解析TableOption节点
     *
     * @param tableOptionNode
     * @param tableDefinition
     */
    private static void parserTableOption(ParseTree tableOptionNode, DbTableDefinition tableDefinition) {
        ParserRuleContext prc = (ParserRuleContext) tableOptionNode;
        if ((tableOptionNode instanceof MySqlParser.TableOptionCommentContext)) {
            if (prc.children != null) {
                if (prc.children != null) {
                    for (int i = 0; i < prc.children.size(); i++) {
                        if ("COMMENT".equalsIgnoreCase(prc.getChild(i).getText())
                                && (i + 1) < prc.children.size()) {
                            tableDefinition.setTableComment(prc.getChild(prc.children.size() - 1).getText()
                                    .replace("\"", "")
                                    .replace("'", "")
                                    .replace("`", "")
                            );
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
    }

    /**
     * 解析CreateDefinitions（表字段）节点
     *
     * @param createDefinitionsNode
     * @param tableDefinition
     */
    private static void parserCreateDefinitions(ParseTree createDefinitionsNode, DbTableDefinition tableDefinition) {
        if (createDefinitionsNode instanceof ParserRuleContext) {
            ParserRuleContext prc = (ParserRuleContext) createDefinitionsNode;
            if (prc.children != null) {
                List<DbTableDefinition.Column> columns = new ArrayList<>();
                for (ParseTree child : prc.children) {
                    if (child instanceof MySqlParser.ColumnDeclarationContext) {
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
    }

    /**
     * 解析字段定义节点
     *
     * @param columnNode
     * @param column
     */
    private static void parserColumns(ParseTree columnNode, DbTableDefinition.Column column) {
        if (columnNode instanceof ParserRuleContext) {
            if (columnNode instanceof MySqlParser.FullColumnNameContext) {
                column.setColumnName(columnNode.getText()
                        .replace("\"", "")
                        .replace("'", "")
                        .replace("`", "")
                        .toLowerCase());
            } else if (columnNode instanceof MySqlParser.DataTypeContext) {
                ParserRuleContext prc = (ParserRuleContext) columnNode;
                if (prc.children != null) {
                    for (ParseTree child : prc.children) {
                        if (child instanceof TerminalNodeImpl) {
                            column.setColumnType(child.getText());
                            break;
                        }
                    }
                }
            } else if (columnNode instanceof MySqlParser.CommentColumnConstraintContext) {
                ParserRuleContext prc = (ParserRuleContext) columnNode;
                if (prc.children != null) {
                    if (prc.children != null) {
                        for (int i = 0; i < prc.children.size(); i++) {
                            if ("COMMENT".equalsIgnoreCase(prc.getChild(i).getText())
                                    && (i + 1) < prc.children.size()) {
                                column.setColumnComment(prc.getChild(i + 1).getText()
                                        .replace("\"", "")
                                        .replace("'", "")
                                        .replace("`", "")
                                );
                                break;
                            }
                        }
                    }
                }
            } else if (columnNode instanceof MySqlParser.PrimaryKeyColumnConstraintContext) {
                column.setPrimaryKey(true);
            } else {
                ParserRuleContext prc = (ParserRuleContext) columnNode;
                if (prc.children != null) {
                    for (ParseTree child : prc.children) {
                        parserColumns(child, column);
                    }
                }
            }
        }
    }
}
