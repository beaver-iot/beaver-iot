package com.milesight.iab.user.request;

import lombok.Data;

/**
 * @author loong
 * @date 2024/10/14 11:23
 */
@Data
public class UserRegisterRequest {

    private String email;
    private String nickName;
    private String password;
    private String confirmPassword;

}
