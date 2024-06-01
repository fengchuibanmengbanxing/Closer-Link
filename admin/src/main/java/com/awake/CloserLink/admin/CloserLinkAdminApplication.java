package com.awake.CloserLink.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author 清醒
 * @Date 2024/5/13 22:15
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class CloserLinkAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloserLinkAdminApplication.class, args);
    }
}

