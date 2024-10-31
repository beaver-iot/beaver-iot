package com.milesight.beaveriot.authentication.po;

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
    private Long authorizationCodeIssuedAt;
    private Long authorizationCodeExpiresAt;
    private String authorizationCodeMetadata;
    private String accessTokenValue;
    private Long accessTokenIssuedAt;
    private Long accessTokenExpiresAt;
    private String accessTokenMetadata;
    private String accessTokenType;
    private String accessTokenScopes;
    private String oidcIdTokenValue;
    private Long oidcIdTokenIssuedAt;
    private Long oidcIdTokenExpiresAt;
    private String oidcIdTokenMetadata;
    private String refreshTokenValue;
    private Long refreshTokenIssuedAt;
    private Long refreshTokenExpiresAt;
    private String refreshTokenMetadata;
    private String userCodeValue;
    private Long userCodeIssuedAt;
    private Long userCodeExpiresAt;
    private String userCodeMetadata;
    private String deviceCodeValue;
    private Long deviceCodeIssuedAt;
    private Long deviceCodeExpiresAt;
    private String deviceCodeMetadata;

}
