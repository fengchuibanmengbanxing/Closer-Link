package com.awake.CloserLink.project.Common.Constant;

/**
 * @Author 清醒
 * @Date 2024/5/20 15:12
 */
public class RedisKeyConstant {
    /**
     * 短链接reidskey
     */
    public static final String GOTO_SHORT_LINK_KEY = "goto_short_link_%s";

    /**
     * 短链接reidskey
     */
    public static final String LOCK_GOTO_SHORT_LINK_KEY = "lock_goto_short_link_%s";
    /**
     * 短链接reidskey
     */
    public static final String GOTO_IS_NOTNULL_SHORT_LINK_KEY = "goto_is_notnull_short_link_key_%s";
    /**
     * 高德地图ip地址api
     */
    public static final String AMAP_REMOTE_KEY = "https://restapi.amap.com/v3/ip";
    /**
     * 短链接修改分组 ID 锁前缀 Key
     */
    public static final String LOCK_GID_UPDATE_KEY = "short-link:lock:update-gid:%s";
}
