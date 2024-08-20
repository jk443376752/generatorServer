package com.tool4j.generator.util.parser.sql.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.tool4j.generator.entity.JavaDefinition;
import com.tool4j.generator.entity.SqlTable;
import com.tool4j.generator.entity.vo.ParseDefinition;
import com.tool4j.generator.entity.vo.ParserResult;
import com.tool4j.generator.util.parser.TextParser;
import com.tool4j.generator.util.parser.SqlParserLoader;
import com.tool4j.generator.util.velocity.VelocityParserPlus;
import lombok.Getter;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterSession;
import net.sf.jsqlparser.statement.alter.AlterSystemStatement;
import net.sf.jsqlparser.statement.alter.RenameTableStatement;
import net.sf.jsqlparser.statement.alter.sequence.AlterSequence;
import net.sf.jsqlparser.statement.comment.Comment;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.schema.CreateSchema;
import net.sf.jsqlparser.statement.create.sequence.CreateSequence;
import net.sf.jsqlparser.statement.create.synonym.CreateSynonym;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.grant.Grant;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.show.ShowTablesStatement;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;

import java.util.ArrayList;
import java.util.List;

@Getter
public class JSqlVisitor implements StatementVisitor, TextParser {

    public static final String CREATE_SQL = "create";
    public static final String SELECT_SQL = "select";
    public static final String INSERT_SQL = "insert";
    public static final String UPDATE_SQL = "update";
    public static final String DELETE_SQL = "delete";

    public static final String COMMENT = "COMMENT";

    private SqlTable sqlTable;

    private String sqlType;

    public static JSqlVisitor newInstance() {
        return new JSqlVisitor();
    }

    @Override
    public void visit(CreateTable createTable) {
        SqlTable sqlTable = new SqlTable();
        sqlTable.setTableName(createTable.getTable().getName())
                .setColumnDefinitions(createTable.getColumnDefinitions());
        List<String> tableOptions = createTable.getTableOptionsStrings();
        if (CollUtil.isNotEmpty(tableOptions)) {
            for (int i = 0; i < tableOptions.size(); i++) {
                if (COMMENT.equalsIgnoreCase(tableOptions.get(i)) && i < tableOptions.size() - 1) {
                    int comment = i + 1;
                    if ("=".equals(tableOptions.get(comment))) {
                        comment++;
                    }
                    sqlTable.setTableComment(tableOptions.get(comment)
                            .replace("'", "")
                            .replace("`", "")
                            .replace("\"", "")
                    );
                    break;
                }
            }
        }
        this.sqlTable = sqlTable;
        this.sqlType = CREATE_SQL;
    }

    @Override
    public void visit(SavepointStatement savepointStatement) {

    }

    @Override
    public void visit(RollbackStatement rollbackStatement) {

    }

    @Override
    public void visit(Comment comment) {

    }

    @Override
    public void visit(Commit commit) {

    }

    @Override
    public void visit(Delete delete) {
        this.sqlType = DELETE_SQL;
    }

    @Override
    public void visit(Update update) {
        this.sqlType = UPDATE_SQL;
    }

    @Override
    public void visit(Insert insert) {
        this.sqlType = INSERT_SQL;
    }

    @Override
    public void visit(Replace replace) {

    }

    @Override
    public void visit(Drop drop) {

    }

    @Override
    public void visit(Truncate truncate) {

    }

    @Override
    public void visit(CreateIndex createIndex) {

    }

    @Override
    public void visit(CreateSchema createSchema) {

    }

    @Override
    public void visit(CreateView createView) {
        this.sqlType = CREATE_SQL;
    }

    @Override
    public void visit(AlterView alterView) {

    }

    @Override
    public void visit(Alter alter) {

    }

    @Override
    public void visit(Statements statements) {

    }

    @Override
    public void visit(Execute execute) {

    }

    @Override
    public void visit(SetStatement setStatement) {

    }

    @Override
    public void visit(ResetStatement resetStatement) {

    }

    @Override
    public void visit(ShowColumnsStatement showColumnsStatement) {

    }

    @Override
    public void visit(ShowTablesStatement showTablesStatement) {

    }

    @Override
    public void visit(Merge merge) {

    }

    @Override
    public void visit(Select select) {
        this.sqlType = SELECT_SQL;
    }

    @Override
    public void visit(Upsert upsert) {
        this.sqlType = UPDATE_SQL;
    }

    @Override
    public void visit(UseStatement useStatement) {

    }

    @Override
    public void visit(Block block) {

    }

    @Override
    public void visit(ValuesStatement valuesStatement) {

    }

    @Override
    public void visit(DescribeStatement describeStatement) {

    }

    @Override
    public void visit(ExplainStatement explainStatement) {

    }

    @Override
    public void visit(ShowStatement showStatement) {

    }

    @Override
    public void visit(DeclareStatement declareStatement) {

    }

    @Override
    public void visit(Grant grant) {

    }

    @Override
    public void visit(CreateSequence createSequence) {

    }

    @Override
    public void visit(AlterSequence alterSequence) {

    }

    @Override
    public void visit(CreateFunctionalStatement createFunctionalStatement) {

    }

    @Override
    public void visit(CreateSynonym createSynonym) {

    }

    @Override
    public void visit(AlterSession alterSession) {

    }

    @Override
    public void visit(IfElseStatement ifElseStatement) {

    }

    @Override
    public void visit(RenameTableStatement renameTableStatement) {

    }

    @Override
    public void visit(PurgeStatement purgeStatement) {

    }

    @Override
    public void visit(AlterSystemStatement alterSystemStatement) {

    }

    @Override
    public ParserResult execute(ParseDefinition parseDefinition) {
        SqlTable sqlTable = SqlUtil.parserSqlTable(parseDefinition.getInput(), parseDefinition.getFields());
        sqlTable.setTableName(sqlTable.getTableName().replace("\"", "")
                .replace("'", "")
                .replace("`", "")
                .toLowerCase());
        List<JavaDefinition.Field> columns = new ArrayList<>();
        sqlTable.getColumnDefinitions().forEach(columnDefinition -> {
            columnDefinition.setColumnName(columnDefinition.getColumnName().replace("\"", "")
                    .replace("'", "")
                    .replace("`", ""));
            columns.add(new JavaDefinition.Field()
                    .setFieldName(StrUtil.toCamelCase(columnDefinition.getColumnName()))
                    .setUpperFieldName(VelocityParserPlus.getUpperName(StrUtil.toCamelCase(columnDefinition.getColumnName())))
                    .setFieldType(SqlUtil.parser2JavaType(columnDefinition.getColDataType().getDataType()))
                    .setDbName(columnDefinition.getColumnName())
                    .setDbType(columnDefinition.getColDataType().getDataType())
                    .setJdbcType(SqlUtil.convertToJdbcType(columnDefinition.getColDataType().getDataType()))
                    .setComment(SqlUtil.getColumnComment(columnDefinition.getColumnSpecs()))
                    .setPrimaryKey(columnDefinition.getColumnSpecs() != null
                            && (columnDefinition.getColumnSpecs().contains("primary") || columnDefinition.getColumnSpecs().contains("PRIMARY")))
            );
        });
        String lowClassName = StrUtil.toCamelCase(sqlTable.getTableName());
        String upperClassName = VelocityParserPlus.getUpperName(lowClassName);
        return new ParserResult()
                .setFileName(upperClassName)
                .setFileType(SqlParserLoader.TEXT_TYPE_JAVA)
                .setTableName(sqlTable.getTableName())
                .setUpperClassName(upperClassName)
                .setLowClassName(lowClassName)
                .setComment(sqlTable.getTableComment())
                .setFields(columns);
    }

}
