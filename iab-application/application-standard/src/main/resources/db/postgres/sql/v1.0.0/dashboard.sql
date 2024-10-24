--liquibase formatted sql

--changeset loong:dashboard_v1.0.0_20241024_095400
CREATE TABLE "t_dashboard"
(
    id         BIGINT PRIMARY KEY,
    name       VARCHAR(255),
    created_at BIGINT,
    updated_at BIGINT
);
CREATE TABLE "t_dashboard_widget"
(
    id           BIGINT PRIMARY KEY,
    dashboard_id BIGINT,
    data         TEXT,
    created_at   BIGINT,
    updated_at   BIGINT
);
CREATE INDEX idx_dashboard_widget_dashboard_id ON "t_dashboard_widget" (dashboard_id);

CREATE TABLE "t_dashboard_widget_template"
(
    id         BIGINT PRIMARY KEY,
    name       VARCHAR(255),
    data       TEXT,
    created_at BIGINT,
    updated_at BIGINT
);