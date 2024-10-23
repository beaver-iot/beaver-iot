package com.milesight.iab.data.jpa.support;

import com.milesight.iab.base.utils.JsonUtils;
import jakarta.persistence.AttributeConverter;

import java.util.Map;

/**
 * @author leon
 */
public class MapJsonConverter implements AttributeConverter<Map<String, Object>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        return JsonUtils.toJSON(attribute);
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        return JsonUtils.toMap(dbData);
    }
}
