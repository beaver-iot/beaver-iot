--liquibase formatted sql

--changeset loong:entity_v1.0.0_20241024_095400
CREATE TABLE "t_entity"
(
    id               BIGINT PRIMARY KEY,
    key              VARCHAR(255) not null,
    name             VARCHAR(255) not null,
    type             VARCHAR(255) not null,
    access_mod       VARCHAR(255),
    parent           VARCHAR(255),
    attach_target    VARCHAR(255) not null,
    attach_target_id VARCHAR(255) not null,
    value_attribute  VARCHAR(255),
    value_type       VARCHAR(255) not null,
    created_at       BIGINT       not null,
    updated_at       BIGINT,
    CONSTRAINT uk_entity_key UNIQUE (key)
);
CREATE INDEX idx_entity_attach_target ON "t_entity" (attach_target_id, attach_target);

CREATE TABLE "t_entity_latest"
(
    id            BIGINT PRIMARY KEY,
    entity_id     BIGINT not null,
    value_int     INTEGER,
    value_float   FLOAT,
    value_boolean BOOLEAN,
    value_string  VARCHAR(255),
    value_binary  BYTEA,
    updated_at    BIGINT
);
CREATE INDEX idx_entity_latest_entity_id ON "t_entity_latest" (entity_id);

CREATE TABLE "t_entity_history"
(
    id            BIGINT PRIMARY KEY,
    entity_id     BIGINT not null,
    value_int     INTEGER,
    value_float   FLOAT,
    value_boolean BOOLEAN,
    value_string  VARCHAR(255),
    value_binary  BYTEA,
    timestamp     BIGINT not null,
    created_at    BIGINT not null,
    created_by    VARCHAR(255),
    updated_at    BIGINT,
    updated_by    VARCHAR(255),
    CONSTRAINT uk_entity_history UNIQUE (entity_id, timestamp)
);