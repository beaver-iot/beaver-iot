package com.milesight.iab.user.facade;

import com.milesight.iab.user.convert.UserConverter;
import com.milesight.iab.user.dto.UserDTO;
import com.milesight.iab.user.po.UserPO;
import com.milesight.iab.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author loong
 * @date 2024/10/14 11:47
 */
@Service
public class UserFacade implements IUserFacade {

    @Autowired
    UserService userService;

    @Override
    public UserDTO getUserByEmail(String email) {
        UserPO userPO = userService.getUserByEmail(email);
        return UserConverter.INSTANCE.convertDTO(userPO);
    }
}
