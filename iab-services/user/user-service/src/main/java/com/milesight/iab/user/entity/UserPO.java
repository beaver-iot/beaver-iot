package com.milesight.iab.user.entity;

import lombok.Data;

/**
 * @author loong
 * @date 2024/10/14 8:42
 */
@Data
public class UserPO {

    private Long id;
    private String email;
    private String emailHash;
    private String nickName;
    private String password;
    private String preference;
    private Long createdAt;
    private Long updatedAt;

}
