package com.milesight.iab.context.integration.entity.annotation;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leon
 */
@Slf4j
public enum AnnotationEntityCache {

    INSTANCE;

    private Map<Method, String> entitiesMethodCache = new ConcurrentHashMap<>();

    public void cacheEntityMethod(Field filed, String key){
        Method getterMethod = getGetterMethod(filed.getDeclaringClass(), filed);
        if(getterMethod != null){
            entitiesMethodCache.put(getterMethod, key);
        }
    }

    public String getEntityKeyByMethod(Method method){
        return entitiesMethodCache.get(method);
    }

    private Method getGetterMethod(Class<?> clazz, Field field) {
        String fieldName = field.getName();
        String capitalizedFieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        String getterName = (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class))? "is" + capitalizedFieldName : "get" + capitalizedFieldName;
        try {
            return clazz.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            log.info("No getter method found for field :" + fieldName + ",This cannot be used to receive ExchangePayload data");
        }
        return null;
    }

}
