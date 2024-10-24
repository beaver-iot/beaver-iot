--liquibase formatted sql

--changeset loong:dashboard_v1.0.0_20241024_095400
CREATE TABLE "dashboard"
(
    id         BIGINT PRIMARY KEY,
    name       VARCHAR(255),
    created_at BIGINT,
    updated_at BIGINT
);
CREATE TABLE "dashboard_widget"
(
    id           BIGINT PRIMARY KEY,
    dashboard_id BIGINT,
    data         TEXT,
    created_at   BIGINT,
    updated_at   BIGINT,
    INDEX        idx_dashboard_widget_dashboard_id (dashboard_id)
);
CREATE TABLE "dashboard_widget_template"
(
    id         BIGINT PRIMARY KEY,
    name       VARCHAR(255),
    data       TEXT,
    created_at BIGINT,
    updated_at BIGINT
);