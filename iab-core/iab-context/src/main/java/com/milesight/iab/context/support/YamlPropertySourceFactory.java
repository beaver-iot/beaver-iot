package com.milesight.iab.context.support;

import com.milesight.iab.base.exception.BootstrapException;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.List;

/**
 * @author leon
 */
public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if (resource == null) {
            throw new BootstrapException("Yaml PropertySource not found:" + name);
        }
        List<PropertySource<?>> sources = new YamlPropertySourceLoader().load(name, resource.getResource());
        return sources.get(0);

    }
}
