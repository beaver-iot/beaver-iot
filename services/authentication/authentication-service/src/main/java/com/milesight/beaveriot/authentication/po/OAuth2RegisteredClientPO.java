package com.milesight.beaveriot.authentication.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

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
    private Long clientIdIssuedAt;
    private String clientSecret;
    private Long clientSecretExpiresAt;
    private String clientName;
    private String clientAuthenticationMethods;
    private String authorizationGrantTypes;
    private String redirectUris;
    private String postLogoutRedirectUris;
    private String scopes;
    private String clientSettings;
    private String tokenSettings;

}
