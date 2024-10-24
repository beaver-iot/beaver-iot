--liquibase formatted sql

--changeset loong:oauth2_v1.0.0_20241024_095400
CREATE TABLE `oauth2_authorization`
(
    id                            VARCHAR(255) PRIMARY KEY,
    registered_client_id          VARCHAR(255),
    principal_name                VARCHAR(255),
    authorization_grant_type      VARCHAR(255),
    authorized_scopes             VARCHAR(255),
    attributes                    VARCHAR(255),
    state                         VARCHAR(255),
    authorization_code_value      VARCHAR(255),
    authorization_code_issued_at  VARCHAR(255),
    authorization_code_expires_at VARCHAR(255),
    authorization_code_metadata   VARCHAR(255),
    access_token_value            VARCHAR(255),
    access_token_issued_at        VARCHAR(255),
    access_token_expires_at       VARCHAR(255),
    access_token_metadata         VARCHAR(255),
    access_token_type             VARCHAR(255),
    access_token_scopes           VARCHAR(255),
    oidc_id_token_value           VARCHAR(255),
    oidc_id_token_issued_at       VARCHAR(255),
    oidc_id_token_expires_at      VARCHAR(255),
    oidc_id_token_metadata        VARCHAR(255),
    refresh_token_value           VARCHAR(255),
    refresh_token_issued_at       VARCHAR(255),
    refresh_token_expires_at      VARCHAR(255),
    refresh_token_metadata        VARCHAR(255),
    user_code_value               VARCHAR(255),
    user_code_issued_at           VARCHAR(255),
    user_code_expires_at          VARCHAR(255),
    user_code_metadata            VARCHAR(255),
    device_code_value             VARCHAR(255),
    device_code_issued_at         VARCHAR(255),
    device_code_expires_at        VARCHAR(255),
    device_code_metadata          VARCHAR(255)
);
CREATE TABLE `oauth2_registered_client`
(
    id                            VARCHAR(255) PRIMARY KEY,
    client_id                     VARCHAR(255),
    client_id_issued_at           TIMESTAMP,
    client_secret                 VARCHAR(255),
    client_secret_expires_at      TIMESTAMP,
    client_name                   VARCHAR(255),
    client_authentication_methods VARCHAR(255),
    authorization_grant_types     VARCHAR(255),
    redirect_uris                 VARCHAR(255),
    post_logout_redirect_uris     VARCHAR(255),
    scopes                        VARCHAR(255),
    client_settings               VARCHAR(255),
    token_settings                VARCHAR(255)
);