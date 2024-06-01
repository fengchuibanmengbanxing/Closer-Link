package com.awake.CloserLink.project;

import com.awake.CloserLink.project.Common.Config.GotoDomainWhiteListConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author 清醒
 * @Date 2024/5/18 11:11
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(value = {GotoDomainWhiteListConfiguration.class})
public class CloserLinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloserLinkApplication.class, args);
    }

}
