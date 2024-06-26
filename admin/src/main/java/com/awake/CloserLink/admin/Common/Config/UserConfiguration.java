package com.awake.CloserLink.admin.Common.Config;

/**
 * @Author 清醒
 * @Date 2024/5/16 22:32
 */

import com.awake.CloserLink.admin.Common.Biz.UserTransmitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用户配置自动装配
 */
@Configuration
public class UserConfiguration {

    /**
     * 用户信息传递过滤器
     */

    @Bean
    public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter() {
        FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserTransmitFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        return registration;
    }
//
//    /**
//     * 用户操作流量风控过滤器
//     */
//    @Bean
//    @ConditionalOnProperty(name = "short-link.flow-limit.enable", havingValue = "true")
//    public FilterRegistrationBean<UserFlowRiskControlFilter> globalUserFlowRiskControlFilter(
//            StringRedisTemplate stringRedisTemplate,
//            UserFlowRiskControlConfiguration userFlowRiskControlConfiguration) {
//        FilterRegistrationBean<UserFlowRiskControlFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(new UserFlowRiskControlFilter(stringRedisTemplate, userFlowRiskControlConfiguration));
//        registration.addUrlPatterns("/*");
//        registration.setOrder(10);
//        return registration;
//    }


}
