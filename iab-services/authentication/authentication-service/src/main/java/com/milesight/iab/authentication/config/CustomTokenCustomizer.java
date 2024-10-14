package com.milesight.iab.authentication.config;

import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

/**
 * @author loong
 * @date 2024/10/12 9:35
 */
@Component
public class CustomTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Resource
    private UserDetailsService userDetailService;

    @Override
    public void customize(JwtEncodingContext context) {
        String username = context.getPrincipal().getName();
        UserDetails user = userDetailService.loadUserByUsername(username);
        //TODO

        if (user != null) {
            context.getClaims().claims(claims -> {
                claims.put("loginName", user.getUsername());
                claims.put("name", user.getUsername());
                claims.put("content", "在accessToken中封装自定义信息");
                claims.put("authorities", "hahahaha");
            });
        }
    }

}
