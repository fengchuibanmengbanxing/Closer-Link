package com.awake.CloserLink.admin.Common.Biz;

/**
 * @Author 清醒
 * @Date 2024/5/16 22:30
 */

import cn.hutool.core.util.StrUtil;
import com.awake.CloserLink.admin.Entitys.UserInfoDTO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * 用户信息传输过滤器
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

//    private final StringRedisTemplate stringRedisTemplate;
//
//    private static final List<String> IGNORE_URI = Lists.newArrayList(
//            "/api/short-link/admin/v1/user/login",
//            "/api/short-link/admin/v1/actual/has-username",
//            "/api/short-link/admin/v1/title"
//    );
    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String username = httpServletRequest.getHeader("username");
        if (StrUtil.isNotBlank(username)) {
            String userId = httpServletRequest.getHeader("userId");
            String realName = httpServletRequest.getHeader("realName");
            UserInfoDTO userInfoDTO = new UserInfoDTO(userId, username, realName);
            UserContext.setUser(userInfoDTO);
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
//
//        String requestURI = httpServletRequest.getRequestURI();
//        if (!IGNORE_URI.contains(requestURI)) {
//            String method = httpServletRequest.getMethod();
//            if (!(("POST".equals(method)) && Objects.equals(requestURI, "/api/short-link/admin/v1/user"))) {
//                String username = httpServletRequest.getHeader("username");
//                String token = httpServletRequest.getHeader("token");
//                if (!StrUtil.isAllNotBlank(username, token)) {
//                    throw new ClientException(UserErrorCodeEnum.IDEMPOTENT_TOKEN_NULL_ERROR);
//                }
//                Object userInfo = null;
//                try {
//                    userInfo = stringRedisTemplate.opsForHash().get("login:" + username, token);
//                    if (userInfo==null){
//                        throw new ClientException(UserErrorCodeEnum.IDEMPOTENT_TOKEN_NULL_ERROR);
//                    }
//                } catch (Exception e) {
//                    throw new ClientException(UserErrorCodeEnum.IDEMPOTENT_TOKEN_NULL_ERROR);
//                }
//
//                UserInfoDTO userInfoDTO =
//                        JSON.parseObject(userInfo.toString(), UserInfoDTO.class);
//                UserContext.setUser(userInfoDTO);
//            }
//        }
//        try {
//            filterChain.doFilter(servletRequest, servletResponse);
//        } finally {
//            UserContext.removeUser();
//        }
//    }
}
