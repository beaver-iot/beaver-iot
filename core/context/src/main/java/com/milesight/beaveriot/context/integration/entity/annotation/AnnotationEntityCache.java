package com.milesight.beaveriot.context.integration.entity.annotation;

import com.milesight.beaveriot.base.constants.StringConstant;
import com.milesight.beaveriot.context.integration.model.Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leon
 */
@Slf4j
public enum AnnotationEntityCache {

    INSTANCE;

    private Map<Method, String> entitiesMethodCache = new ConcurrentHashMap<>();

    private Map<String, List<Entity>> deviceTemplateEntitiesCache = new ConcurrentHashMap<>();

    public void cacheDeviceTemplateEntities(String integrationId, String deviceName, List<Entity> entities) {
        if (ObjectUtils.isEmpty(entities)) {
            return;
        }
        String key = generateTemplateCacheKey(integrationId, deviceName);
        if (deviceTemplateEntitiesCache.containsKey(key)) {
            deviceTemplateEntitiesCache.get(key).addAll(entities);
        } else {
            deviceTemplateEntitiesCache.put(key, entities);
        }
    }

    private String generateTemplateCacheKey(String integrationId, String deviceName) {
        return integrationId + StringConstant.AT + deviceName;
    }

    public List<Entity> getDeviceTemplateEntities(String integrationId, String deviceName) {
        return deviceTemplateEntitiesCache.get(generateTemplateCacheKey(integrationId, deviceName));
    }

    public void cacheEntityMethod(Field filed, String key) {
        Method getterMethod = getGetterMethod(filed.getDeclaringClass(), filed);
        if (getterMethod != null) {
            entitiesMethodCache.put(getterMethod, key);
        }
    }

    public String getEntityKeyByMethod(Method method) {
        return entitiesMethodCache.get(method);
    }

    private Method getGetterMethod(Class<?> clazz, Field field) {
        String fieldName = field.getName();
        String capitalizedFieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        String getterName = "get" + capitalizedFieldName;
        try {
            return clazz.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                try {
                    String booleanGetterName = "is" + capitalizedFieldName;
                    return clazz.getMethod(booleanGetterName);
                } catch (NoSuchMethodException ex) {
                    log.info("No getter method found for field :" + fieldName + ",This cannot be used to receive ExchangePayload data");
                }
            }
            log.info("No getter method found for field :" + fieldName + ",This cannot be used to receive ExchangePayload data");
        }
        return null;
    }

}
