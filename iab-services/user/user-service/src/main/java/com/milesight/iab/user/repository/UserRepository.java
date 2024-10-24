package com.milesight.iab.user.repository;

import com.milesight.iab.data.api.BaseRepository;
import com.milesight.iab.data.jpa.repository.BaseJpaRepository;
import com.milesight.iab.user.po.UserPO;
import org.springframework.stereotype.Repository;

/**
 * @author loong
 * @date 2024/10/14 10:55
 */
public interface UserRepository extends BaseJpaRepository<UserPO, Long> {
}
