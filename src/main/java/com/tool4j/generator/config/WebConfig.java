package com.tool4j.generator.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WebMvc 配置类
 *
 * @since 0.0.1
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private GlobalExceptionResolver globalExceptionResolver;

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(0, globalExceptionResolver);
    }
}
