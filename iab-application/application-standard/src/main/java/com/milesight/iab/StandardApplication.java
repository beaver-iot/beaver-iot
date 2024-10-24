package com.milesight.iab;

import com.milesight.iab.data.jpa.BaseJpaRepositoryImpl;
import com.milesight.iab.data.jpa.repository.BaseJpaRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author leon
 */

//@EnableJpaRepositories
@EnableJpaRepositories(
        repositoryBaseClass = BaseJpaRepositoryImpl.class
//        repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class,
//        basePackages = {
//                "com.yeastar.cloud.dm.server.security.repository",
//                "com.yeastar.cloud.dm.server.repository"
//        }
        )
@SpringBootApplication
public class StandardApplication {

    public static void main(String[] args) {
        SpringApplication.run(StandardApplication.class, args);
    }

}
