package com.tool4j.generator.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.List;

@Data
@Accessors(chain = true)
public class SqlTable {

    private String tableName;

    private String tableComment;

    private List<ColumnDefinition> columnDefinitions;

}
