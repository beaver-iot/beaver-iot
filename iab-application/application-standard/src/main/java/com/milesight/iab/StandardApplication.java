package com.milesight.iab;

import com.milesight.iab.data.jpa.BaseJpaRepositoryImpl;
import com.milesight.iab.data.jpa.repository.BaseJpaRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author leon
 */

@EnableJpaRepositories(repositoryBaseClass = BaseJpaRepositoryImpl.class )
@SpringBootApplication
public class StandardApplication {

    public static void main(String[] args) {
        SpringApplication.run(StandardApplication.class, args);
    }

}
