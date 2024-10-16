package com.milesight.iab.context.integration.entity.annotation;

import com.milesight.iab.context.integration.builder.DeviceBuilder;
import com.milesight.iab.context.integration.builder.EntityBuilder;
import com.milesight.iab.context.integration.entity.EntityLoader;
import com.milesight.iab.context.integration.enums.EntityValueType;
import com.milesight.iab.context.integration.model.Integration;
import com.milesight.iab.context.support.EnhancePropertySourcesPropertyResolver;
import com.milesight.iab.context.support.PackagesScanner;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author leon
 */
public class AnnotationEntityLoader implements EntityLoader {

    public static final String REPLACE_HOLDER_FIELD_NAME = "fieldName";

    private PackagesScanner packagesScanner;

    public AnnotationEntityLoader() {
        this.packagesScanner = new PackagesScanner();
    }

    @Override
    public void load(Integration integration, StandardEnvironment integrationEnvironment) {
        // scan annotation class
        EnhancePropertySourcesPropertyResolver propertyResolver = new EnhancePropertySourcesPropertyResolver(integrationEnvironment.getPropertySources());
        packagesScanner.doScan(integration.getIntegrationClass().getPackage().getName(), clazz -> {

            if (clazz.isAnnotationPresent(DeviceEntities.class)) {
                // parse DeviceEntities annotation
                DeviceEntities deviceEntitiesAnno = clazz.getAnnotation(DeviceEntities.class);
                DeviceBuilder deviceBuilder = new DeviceBuilder().name(deviceEntitiesAnno.name())
                        .identifier(deviceEntitiesAnno.identifier())
                        .additional(resolveKeyValue(deviceEntitiesAnno.additional()));
                List<com.milesight.iab.context.integration.model.Entity> entities = parserEntities(clazz, propertyResolver);
                deviceBuilder.entities(entities);
                integration.addDevice(deviceBuilder.build());

            } else if (clazz.isAnnotationPresent(IntegrationEntities.class)) {
                // parse IntegrationEntities annotation
                List<com.milesight.iab.context.integration.model.Entity> entities = parserEntities(clazz, propertyResolver);
                integration.addEntities(entities);
            }
        });
    }

    private List<com.milesight.iab.context.integration.model.Entity> parserEntities(Class<?> clazz, EnhancePropertySourcesPropertyResolver propertyResolver) {
        List<com.milesight.iab.context.integration.model.Entity> entities = new ArrayList<>();
        ReflectionUtils.doWithFields(clazz, field -> {
            Entity entityAnnotation = field.getAnnotation(Entity.class);
            if (entityAnnotation != null) {
                EntityValueType valueType = EntityValueType.of(field.getType());
                String name = resolvePlaceholders(entityAnnotation.name(), field, propertyResolver);
                String identifier = resolvePlaceholders(entityAnnotation.identifier(), field, propertyResolver);
                EntityBuilder entityBuilder = new EntityBuilder()
                        .identifier(resolvePlaceholders(entityAnnotation.identifier(), field, propertyResolver))
                        .type(entityAnnotation.type())
                        .attributes(resolveKeyValue(entityAnnotation.attributes()))
                        .valueType(valueType);
                switch (entityAnnotation.type()) {
                    case EVENT:
                        entityBuilder.event(name);
                        break;
                    case PROPERTY:
                        entityBuilder.property(name, entityAnnotation.accessMod());
                        break;
                    case SERVICE:
                        entityBuilder.service(name, entityAnnotation.syncCall());
                        break;
                    default:
                        break;
                }
                if (valueType == EntityValueType.OBJECT) {
                    List<com.milesight.iab.context.integration.model.Entity> children = parserEntities(field.getType(), propertyResolver);
                    children.forEach(entity -> {
                        entity.setGroup(identifier);
                        entity.setType(entityAnnotation.type());
                        entity.setAccessMod(entityAnnotation.accessMod());
                        entity.setSyncCall(entityAnnotation.syncCall());
                    });
                    entityBuilder.children(children);
                }
                entities.add(entityBuilder.build());
            }
        });
        return entities;
    }

    private Map<String, Object> resolveKeyValue(KeyValue[] keyValues) {
        return ObjectUtils.isEmpty(keyValues) ? Map.of() : Arrays.stream(keyValues).collect(Collectors.toMap(KeyValue::key, KeyValue::value));
    }

    private String resolvePlaceholders(String value, Field field, EnhancePropertySourcesPropertyResolver propertyResolver) {
        Map<String, Object> dynamicProperties = Map.of(REPLACE_HOLDER_FIELD_NAME, field.getName());
        return propertyResolver.resolvePlaceholders(value, dynamicProperties);
    }

}
