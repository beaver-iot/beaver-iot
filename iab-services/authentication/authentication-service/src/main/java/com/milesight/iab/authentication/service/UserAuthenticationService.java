package com.milesight.iab.authentication.service;

import com.milesight.iab.authentication.util.OAuth2EndpointUtils;
import com.milesight.iab.context.security.SecurityUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Service;

/**
 * @author loong
 * @date 2024/10/14 11:37
 */
@Service
public class UserAuthenticationService {

    @Autowired
    OAuth2AuthorizationService authorizationService;
    @Autowired
    JwtDecoder jwtDecoder;

    public void readAccessToken(String accessToken) {
        OAuth2Authorization authorization = authorizationService.findByToken(accessToken, OAuth2TokenType.ACCESS_TOKEN);
        if (authorization == null) {
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, "Invalid access token", null);
        }
        Jwt jwt = jwtDecoder.decode(accessToken);

        SecurityUserContext.SecurityUser securityUser = SecurityUserContext.SecurityUser.builder()
                .accessToken(accessToken)
                .header(jwt.getHeaders())
                .payload(jwt.getClaims())
                .build();
        SecurityUserContext.setSecurityUser(securityUser);
    }

}
