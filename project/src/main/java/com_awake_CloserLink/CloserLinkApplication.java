package com_awake_CloserLink;

import com_awake_CloserLink.Common.Config.GotoDomainWhiteListConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @Author 清醒
 * @Date 2024/5/18 11:11
 */
@SpringBootApplication
@EnableConfigurationProperties(value = {GotoDomainWhiteListConfiguration.class})
public class CloserLinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloserLinkApplication.class, args);
    }

}
