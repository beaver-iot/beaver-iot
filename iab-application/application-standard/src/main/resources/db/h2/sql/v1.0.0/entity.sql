--liquibase formatted sql

--changeset loong:entity_v1.0.0_20241024_095400
CREATE TABLE `entity`
(
    id               BIGINT PRIMARY KEY,
    key              VARCHAR(255),
    name             VARCHAR(255),
    type             VARCHAR(255),
    access_mod       VARCHAR(255),
    sync_call        BOOLEAN,
    parent           VARCHAR(255),
    attach_target    VARCHAR(255),
    attach_target_id VARCHAR(255),
    value_attribute  VARCHAR(255),
    value_type       VARCHAR(255),
    created_at       BIGINT,
    updated_at       BIGINT,
    CONSTRAINT uk_entity_key UNIQUE (key),
    INDEX            idx_entity_attach_target (attach_target_id, attach_target)
);
CREATE TABLE `entity_latest`
(
    id            BIGINT PRIMARY KEY,
    entity_id     BIGINT,
    value_int     INTEGER,
    value_float   FLOAT,
    value_boolean BOOLEAN,
    value_string  VARCHAR(255),
    value_binary  BLOB,
    updated_at    BIGINT,
    INDEX         idx_entity_latest_entity_id (entity_id)
);
CREATE TABLE `entity_history`
(
    id            BIGINT PRIMARY KEY,
    entity_id     BIGINT,
    value_int     INTEGER,
    value_float   FLOAT,
    value_boolean BOOLEAN,
    value_string  VARCHAR(255),
    value_binary  BLOB,
    timestamp     BIGINT,
    created_at    BIGINT,
    created_by    VARCHAR(255),
    updated_at    BIGINT,
    updated_by    VARCHAR(255),
    CONSTRAINT uk_entity_history UNIQUE (entity_id, timestamp)
);
