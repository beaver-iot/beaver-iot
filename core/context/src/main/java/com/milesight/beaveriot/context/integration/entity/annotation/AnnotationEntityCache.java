package com.milesight.beaveriot.context.integration.entity.annotation;

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
        String getterName = "get" + capitalizedFieldName;
        try {
            return clazz.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)){
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
