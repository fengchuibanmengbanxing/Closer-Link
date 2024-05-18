package com_awake_CloserLink.Common.Biz;

/**
 * @Author 清醒
 * @Date 2024/5/16 22:30
 */

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com_awake_CloserLink.Common.Convention.Exception.ClientException;
import com_awake_CloserLink.Entitys.UserInfoDTO;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com_awake_CloserLink.Common.Enums.UserErrorCodeEnum.IDEMPOTENT_TOKEN_NULL_ERROR;

/**
 * 用户信息传输过滤器
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    private static final List<String> IGNORE_URI = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/actual/has-username"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String requestURI = httpServletRequest.getRequestURI();
        if (!IGNORE_URI.contains(requestURI)) {
            String method = httpServletRequest.getMethod();
            if (!("POST".equals(method)) && Objects.equals(requestURI, "/api/short-link/admin/v1/user")) {
                String username = httpServletRequest.getHeader("username");
                String token = httpServletRequest.getHeader("token");
                if (!StrUtil.isAllNotBlank(username, token)) {
                    throw new ClientException(IDEMPOTENT_TOKEN_NULL_ERROR);
                }
                Object userInfo = null;
                try {
                    userInfo = stringRedisTemplate.opsForHash().get("login:" + username, token);
                    if (userInfo==null){
                        throw new ClientException(IDEMPOTENT_TOKEN_NULL_ERROR);
                    }
                } catch (Exception e) {
                    throw new ClientException(IDEMPOTENT_TOKEN_NULL_ERROR);
                }


                UserInfoDTO userInfoDTO =
                        JSON.parseObject(userInfo.toString(), UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);

            }
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
