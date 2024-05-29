package com_awake_CloserLink.Common.Constant;

/**
 * @Author 清醒
 * @Date 2024/5/15 17:17
 */
public class RedisCacheConstant {
public static final String LOCK_USER_REGISTER_KEY="closer-link:lock_user:";
    /**
     * 用户分组创建锁（防止多端登录创建）
     */
    public static final String LOCK_GROUP_CREATE_KEY="closer-link:group:%s";
}
