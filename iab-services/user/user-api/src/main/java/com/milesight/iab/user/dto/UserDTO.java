package com.milesight.iab.user.dto;

import lombok.Data;

/**
 * @author loong
 * @date 2024/10/17 13:41
 */
@Data
public class UserDTO {

    private String userId;
    private String email;
    private String nickname;
    private String encodePassword;
    private String preference;

}
