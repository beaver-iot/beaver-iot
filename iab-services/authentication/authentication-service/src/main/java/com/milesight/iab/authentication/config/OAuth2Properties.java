package com.milesight.iab.authentication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author loong
 * @date 2024/10/24 14:39
 */
@Component
@Data
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Properties {

    private List<String> permitUrls;

}
