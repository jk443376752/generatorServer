package com.tool4j.generator.util.velocity;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

import java.io.InputStream;

@Slf4j
public class VelocityTemplateLoader extends ResourceLoader {

    @Override
    public void init(ExtendedProperties configuration) {
    }

    @Override
    public InputStream getResourceStream(String source) throws ResourceNotFoundException {
        try {
            String filePath = String.format("template/%s", source);
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        } catch (Exception e) {
            return this.getClass().getResourceAsStream(source);
        }
    }

    @Override
    public boolean isSourceModified(Resource resource) {
        return true;
    }

    @Override
    public long getLastModified(Resource resource) {
        return 0;
    }

}