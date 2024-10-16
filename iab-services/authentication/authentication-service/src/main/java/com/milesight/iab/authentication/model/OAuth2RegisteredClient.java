package com.milesight.iab.authentication.model;

import lombok.Data;

import java.util.Date;

/**
 * @author loong
 * @date 2024/10/14 9:40
 */
@Data
public class OAuth2RegisteredClient {

    private String id;
    private String clientId;
    private Date clientIdIssuedAt;
    private String clientSecret;
    private Date clientSecretExpiresAt;
    private String clientName;
    private String clientAuthenticationMethods;
    private String authorizationGrantTypes;
    private String redirectUris;
    private String postLogoutRedirectUris;
    private String scopes;
    private String clientSettings;
    private String tokenSettings;

}
