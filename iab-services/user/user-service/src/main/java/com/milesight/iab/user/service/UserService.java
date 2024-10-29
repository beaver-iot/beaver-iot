package com.milesight.iab.user.service;

import com.milesight.iab.base.enums.ErrorCode;
import com.milesight.iab.base.exception.ServiceException;
import com.milesight.iab.base.utils.snowflake.SnowflakeUtil;
import com.milesight.iab.context.security.SecurityUserContext;
import com.milesight.iab.user.enums.UserErrorCode;
import com.milesight.iab.user.model.request.UserRegisterRequest;
import com.milesight.iab.user.model.response.UserInfoResponse;
import com.milesight.iab.user.po.UserPO;
import com.milesight.iab.user.repository.UserRepository;
import com.milesight.iab.user.util.SignUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author loong
 * @date 2024/10/14 8:42
 */
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void register(UserRegisterRequest userRegisterRequest) {
        String email = userRegisterRequest.getEmail();
        String nickname = userRegisterRequest.getNickname();
        String password = userRegisterRequest.getPassword();
        if (!StringUtils.hasText(email) || !StringUtils.hasText(nickname) || !StringUtils.hasText(password)) {
            throw ServiceException.with(ErrorCode.PARAMETER_SYNTAX_ERROR).detailMessage("email and nickname and password must be not null").build();
        }
        UserPO userPO = getUserByEmail(email);
        if (userPO != null) {
            throw ServiceException.with(UserErrorCode.USER_REGISTER_EMAIL_EXIST).build();
        }
        userPO = new UserPO();
        userPO.setId(SnowflakeUtil.nextId());
        userPO.setEmail(email);
        userPO.setEmailHash(SignUtils.sha256Hex(email));
        userPO.setNickname(nickname);
        userPO.setPassword(new BCryptPasswordEncoder().encode(password));
        userPO.setPreference(null);
        userRepository.save(userPO);
    }

    public UserInfoResponse getUserInfo() {
        SecurityUserContext.SecurityUser securityUser = SecurityUserContext.getSecurityUser();
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        if (securityUser != null) {
            userInfoResponse.setUserId(SecurityUserContext.getUserId());
            userInfoResponse.setNickname(securityUser.getPayload().get("nickname").toString());
            userInfoResponse.setEmail(securityUser.getPayload().get("email").toString());
        }
        return userInfoResponse;
    }

    public UserPO getUserByEmail(String email) {
        return userRepository.findOne(filter -> filter.eq(UserPO.Fields.email, email)).orElse(null);
    }

}
