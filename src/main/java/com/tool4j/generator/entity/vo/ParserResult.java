package com.tool4j.generator.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tool4j.generator.entity.JavaDefinition;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 文本解析响应实体类
 */
@Data
@Accessors(chain = true)
public class ParserResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fileName;

    private String fileType;

    private String input;

    private String content;

    private List<String> columns;

    private boolean errored;

    @JsonIgnore
    private String upperClassName;

    @JsonIgnore
    private String lowClassName;

    @JsonIgnore
    private String comment;

    @JsonIgnore
    private String tableName;

    @JsonIgnore
    private List<JavaDefinition.Field> fields;

    public ParserResult(String fileName, String fileType, String content) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.content = content;
    }

    public ParserResult(String fileName, String fileType, String content, Boolean errored) {
        this(fileName, fileType, content);
        this.errored = errored;
    }

    public ParserResult() {

    }
}
