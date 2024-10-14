package com.milesight.iab.authentication.facade;

import com.milesight.iab.authentication.IUserAuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
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
public class UserAuthenticationFacade implements IUserAuthenticationFacade {

    @Autowired
    OAuth2AuthorizationService authorizationService;
    @Autowired
    JwtDecoder jwtDecoder;

    public void readAccessToken(String accessToken) {
        OAuth2Authorization authorization = authorizationService.findByToken(accessToken, OAuth2TokenType.ACCESS_TOKEN);
        if (authorization == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT);
        }
        Jwt jwt = jwtDecoder.decode(accessToken);
        jwt.getHeaders().forEach((k, v) -> {
            //TODO
            System.out.println(k + ":" + v);
        });
        jwt.getClaims().forEach((k, v) -> {
            //TODO
            System.out.println(k + ":" + v);
        });
    }

}
