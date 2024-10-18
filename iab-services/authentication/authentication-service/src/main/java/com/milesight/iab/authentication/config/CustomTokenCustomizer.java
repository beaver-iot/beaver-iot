package com.milesight.iab.authentication.config;

import com.milesight.iab.authentication.util.OAuth2EndpointUtils;
import com.milesight.iab.user.dto.UserDTO;
import com.milesight.iab.user.facade.IUserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

/**
 * @author loong
 * @date 2024/10/12 9:35
 */
@Component
public class CustomTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Autowired
    IUserFacade userFacade;

    @Override
    public void customize(JwtEncodingContext context) {
        String username = context.getPrincipal().getName();
        UserDTO userDTO = userFacade.getUserByEmail(username);
        if(userDTO == null){
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, "user not found.", null);
        }
        context.getClaims().claims(claims -> {
            claims.put("userId", userDTO.getUserId());
            claims.put("nickname", userDTO.getNickname());
            claims.put("email", userDTO.getEmail());
        });
    }

}
