--liquibase formatted sql

--changeset loong:dashboard_v1.0.0_20241024_095400
CREATE TABLE `t_dashboard`
(
    id         BIGINT PRIMARY KEY,
    name       VARCHAR(255),
    created_at BIGINT,
    updated_at BIGINT
);
CREATE TABLE `t_dashboard_widget`
(
    id           BIGINT PRIMARY KEY,
    dashboard_id BIGINT,
    data         CLOB,
    created_at   BIGINT,
    updated_at   BIGINT,
    INDEX        idx_dashboard_widget_dashboard_id (dashboard_id)
);
CREATE TABLE `t_dashboard_widget_template`
(
    id         BIGINT PRIMARY KEY,
    name       VARCHAR(255),
    data       CLOB,
    created_at BIGINT,
    updated_at BIGINT
);