package com.tool4j.generator.config;

import com.tool4j.generator.entity.TemplateInfo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "template")
@Data
public class TemplateConfig {

    private List<TemplateInfo.FileInfo> mybatis;

    private List<TemplateInfo.FileInfo> mybatisPlus;

    private List<TemplateInfo.FileInfo> jpa;
}
