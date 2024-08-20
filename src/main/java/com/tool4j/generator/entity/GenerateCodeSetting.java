package com.tool4j.generator.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 代码生成配置
 *
 * @author Deng.Weiping
 * @since 2023-11-01 14:13:15
 */
@Data
@ApiModel(value = "GenerateCodeSetting", description = "代码生成配置表")
public class GenerateCodeSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long userId;

    /**
     * 配置内容
     */
    @ApiModelProperty("配置内容")
    private String setting;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createdDate;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date updatedDate;

}