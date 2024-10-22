package com.milesight.iab.user.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author loong
 * @date 2024/10/14 8:42
 */
@Data
@Table(name = "user")
@Entity
@FieldNameConstants
public class UserPO {

    @Id
    private Long id;
    private String email;
    private String emailHash;
    private String nickname;
    private String password;
    private String preference;
    private Long createdAt;
    private Long updatedAt;

}
