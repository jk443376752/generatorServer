package com.tool4j.generator.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 代码生成模板信息表
 *
 * @author Deng.Weiping
 * @since 2023-11-24 09:02:30
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "TemplateInfo", description = "代码生成模板信息表")
public class TemplateInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ORM 框架类型
     */
    @ApiModelProperty("ORM 框架类型")
    private String ormType;

    /**
     * 路径
     */
    @ApiModelProperty("路径")
    private String path;

    /**
     * 模板文件集合
     */
    @ApiModelProperty("模板文件集合")
    private List<FileInfo> files;

    @Data
    @Accessors(chain = true)
    public static class FileInfo {

        /**
         * 文件名
         */
        @ApiModelProperty("文件名")
        private String fileName;

        /**
         * 路径
         */
        @ApiModelProperty("路径")
        private String filePath;

    }

}