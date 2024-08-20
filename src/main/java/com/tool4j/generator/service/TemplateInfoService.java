package com.tool4j.generator.service;

import com.tool4j.generator.config.TemplateConfig;
import com.tool4j.generator.entity.TemplateInfo;
import com.tool4j.generator.util.generate.JavaCodeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码模板逻辑类
 *
 * @since 0.0.1
 */
@Service
public class TemplateInfoService {

    @Autowired
    private TemplateConfig templateConfig;

    public TemplateInfo getTemplateInfo(String ormType) {
        switch (ormType) {
            case JavaCodeConfig.ORM_TYPE_JPA:
                return new TemplateInfo()
                        .setPath(JavaCodeConfig.ORM_TYPE_JPA)
                        .setFiles(templateConfig.getJpa());
            case JavaCodeConfig.ORM_TYPE_MYBATIS:
                return new TemplateInfo()
                        .setPath(JavaCodeConfig.ORM_TYPE_MYBATIS)
                        .setFiles(templateConfig.getMybatis());
            case JavaCodeConfig.ORM_TYPE_MYBATIS_PLUS:
                return new TemplateInfo()
                        .setPath(JavaCodeConfig.ORM_TYPE_MYBATIS_PLUS)
                        .setFiles(templateConfig.getMybatisPlus());
            default:
                return null;
        }
    }

    public List<TemplateInfo> findAll() {
        List<TemplateInfo> result = new ArrayList<>();
        result.add(new TemplateInfo()
                .setOrmType(JavaCodeConfig.ORM_TYPE_JPA)
                .setPath(JavaCodeConfig.ORM_TYPE_JPA)
                .setFiles(templateConfig.getJpa()));
        result.add(new TemplateInfo()
                .setOrmType(JavaCodeConfig.ORM_TYPE_MYBATIS)
                .setPath(JavaCodeConfig.ORM_TYPE_MYBATIS)
                .setFiles(templateConfig.getMybatis()));
        result.add(new TemplateInfo()
                .setOrmType(JavaCodeConfig.ORM_TYPE_MYBATIS_PLUS)
                .setPath(JavaCodeConfig.ORM_TYPE_MYBATIS_PLUS)
                .setFiles(templateConfig.getMybatisPlus()));
        return result;
    }
}