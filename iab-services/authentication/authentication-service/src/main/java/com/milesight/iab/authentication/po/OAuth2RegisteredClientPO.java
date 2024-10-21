package com.milesight.iab.authentication.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * @author loong
 * @date 2024/10/14 9:40
 */
@Data
@Table(name = "oauth2_registered_client")
@Entity
@FieldNameConstants
public class OAuth2RegisteredClientPO {

    @Id
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
