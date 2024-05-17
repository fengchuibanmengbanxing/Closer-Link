package com_awake_CloserLink.Common.Biz;

/**
 * @Author 清醒
 * @Date 2024/5/16 22:30
 */

import com.alibaba.fastjson2.JSON;
import com_awake_CloserLink.Entitys.UserInfoDTO;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;

/**
 * 用户信息传输过滤器
 */
public class UserTransmitFilter implements Filter {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String username = httpServletRequest.getHeader("username");
        String token = httpServletRequest.getHeader("token");
        Object userInfo = stringRedisTemplate.opsForHash().get("login:" + username, token);
        if (userInfo != null) {
            UserInfoDTO userInfoDTO =
                    JSON.parseObject(userInfo.toString(), UserInfoDTO.class);
            UserContext.setUser(userInfoDTO);
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
