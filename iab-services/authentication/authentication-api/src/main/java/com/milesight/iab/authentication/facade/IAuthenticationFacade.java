package com.milesight.iab.authentication.facade;

/**
 * @author loong
 * @date 2024/10/23 13:10
 */
public interface IAuthenticationFacade {

    String getUserIdByToken(String token);

}
