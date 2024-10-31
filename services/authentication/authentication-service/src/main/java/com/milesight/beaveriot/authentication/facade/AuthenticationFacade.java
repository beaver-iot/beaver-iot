package com.milesight.beaveriot.authentication.facade;

import com.milesight.beaveriot.authentication.service.UserAuthenticationService;
import com.milesight.beaveriot.context.security.SecurityUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author loong
 * @date 2024/10/23 13:11
 */
@Service
public class AuthenticationFacade implements IAuthenticationFacade {

    @Autowired
    UserAuthenticationService userAuthenticationService;

    @Override
    public String getUserIdByToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        Jwt jwt = userAuthenticationService.readAccessToken(token);
        return jwt.getClaims().get(SecurityUserContext.USER_ID).toString();
    }

}
