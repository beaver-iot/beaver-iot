package com.milesight.iab.user.model;

import lombok.Data;

/**
 * @author loong
 * @date 2024/10/14 8:42
 */
@Data
public class User {

    private Long id;
    private String email;
    private String emailHash;
    private String nickName;
    private String password;
    private String preference;
    private Long createdAt;
    private Long updatedAt;

}
