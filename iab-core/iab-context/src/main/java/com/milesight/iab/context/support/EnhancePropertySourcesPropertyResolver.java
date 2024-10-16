package com.milesight.iab.context.support;

import org.springframework.core.env.PropertySources;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.util.ObjectUtils;
import org.springframework.util.SystemPropertyUtils;

import java.util.Map;

/**
 * @author leon
 */
public class EnhancePropertySourcesPropertyResolver extends PropertySourcesPropertyResolver {
    /**
     * Create a new resolver against the given property sources.
     *
     * @param propertySources the set of {@link PropertySources} objects to use
     */
    public EnhancePropertySourcesPropertyResolver(PropertySources propertySources) {
        super(propertySources);
    }

    public String resolvePlaceholders(String text, Map<String,Object> propertySource) {

        String value = super.resolvePlaceholders(text);
        if(value.contains(SystemPropertyUtils.PLACEHOLDER_PREFIX ) && !ObjectUtils.isEmpty(propertySource)){
            String removePlaceholder = text.replace(SystemPropertyUtils.PLACEHOLDER_PREFIX, "").replace(SystemPropertyUtils.PLACEHOLDER_SUFFIX, "");
            return (String) propertySource.get(removePlaceholder);
        }else{
            return value;
        }

    }
}
