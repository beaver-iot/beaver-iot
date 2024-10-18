package com.milesight.iab.authentication.filter;

import com.milesight.iab.authentication.service.UserAuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author loong
 * @date 2024/10/17 9:25
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    UserAuthenticationService userAuthenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationValue = request.getHeader("Authorization");

        if (authorizationValue != null && authorizationValue.startsWith("Bearer ")) {
            String token = authorizationValue.substring(7);

            userAuthenticationService.readAccessToken(token);
        }
        filterChain.doFilter(request, response);
    }
}
