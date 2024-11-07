package com.milesight.beaveriot.context.integration.model;

import com.milesight.beaveriot.context.integration.enums.AccessMod;
import com.milesight.beaveriot.context.integration.enums.EntityType;
import com.milesight.beaveriot.context.integration.enums.EntityValueType;
import com.milesight.beaveriot.context.support.IdentifierValidator;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author leon
 */
public class BaseEntityBuilder<T extends BaseEntityBuilder> {
    protected Long id;
    protected String name;

    protected String identifier;
    protected AccessMod accessMod;

    protected EntityType type;
    protected EntityValueType valueType;
    protected Map<String, Object> attributes;
    protected String parentIdentifier;

    public T identifier(String identifier) {
        IdentifierValidator.isValid(identifier);
        this.identifier = identifier;
        return (T) this;
    }

    public T parentIdentifier(String parentIdentifier) {
        IdentifierValidator.isValidNullable(parentIdentifier);
        this.parentIdentifier = parentIdentifier;
        return (T) this;
    }

    public T id(Long id) {
        this.id = id;
        return (T) this;
    }

    public T valueType(EntityValueType valueType) {
        this.valueType = valueType;
        return (T) this;
    }

    public T attributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return (T) this;
    }

    public T property(String name, AccessMod accessMod) {
        this.name = name;
        this.accessMod = accessMod;
        this.type = EntityType.PROPERTY;
        if (!StringUtils.hasLength(identifier)) {
            this.identifier = name;
        }
        return (T) this;
    }

    public T service(String name) {
        this.name = name;
        this.type = EntityType.SERVICE;
        if (!StringUtils.hasLength(identifier)) {
            this.identifier = name;
        }
        return (T) this;
    }

    public T event(String name) {
        this.name = name;
        this.type = EntityType.EVENT;
        if (!StringUtils.hasLength(identifier)) {
            this.identifier = name;
        }
        return (T) this;
    }

    protected Entity newInstance() {
        Entity entity = new Entity();
        entity.setId(id);
        entity.setName(name);
        entity.setIdentifier(identifier);
        entity.setAccessMod(accessMod);
        entity.setValueType(valueType);
        entity.setType(type);
        entity.setAttributes(attributes);
        entity.setParentIdentifier(parentIdentifier);
        return entity;
    }

}
