package com.milesight.iab.authentication.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author loong
 * @date 2024/10/14 9:43
 */
@Data
@Table(name = "oauth2_authorization")
@Entity
@FieldNameConstants
public class OAuth2AuthorizationPO {

    @Id
    private String id;
    private String registeredClientId;
    private String principalName;
    private String authorizationGrantType;
    private String authorizedScopes;
    private String attributes;
    private String state;
    private String authorizationCodeValue;
    private String authorizationCodeIssuedAt;
    private String authorizationCodeExpiresAt;
    private String authorizationCodeMetadata;
    private String accessTokenValue;
    private String accessTokenIssuedAt;
    private String accessTokenExpiresAt;
    private String accessTokenMetadata;
    private String accessTokenType;
    private String accessTokenScopes;
    private String oidcIdTokenValue;
    private String oidcIdTokenIssuedAt;
    private String oidcIdTokenExpiresAt;
    private String oidcIdTokenMetadata;
    private String refreshTokenValue;
    private String refreshTokenIssuedAt;
    private String refreshTokenExpiresAt;
    private String refreshTokenMetadata;
    private String userCodeValue;
    private String userCodeIssuedAt;
    private String userCodeExpiresAt;
    private String userCodeMetadata;
    private String deviceCodeValue;
    private String deviceCodeIssuedAt;
    private String deviceCodeExpiresAt;
    private String deviceCodeMetadata;

}
