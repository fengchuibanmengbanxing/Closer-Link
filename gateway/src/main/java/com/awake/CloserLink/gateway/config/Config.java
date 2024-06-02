package com.awake.CloserLink.gateway.config;

/**
 * @Author 清醒
 * @Date 2024/6/1 21:18
 */

import lombok.Data;

import java.util.List;

/**
 * 过滤器配置
 */
@Data
public class Config {
    /**
     * 白名单前置路径
     */
    private List<String> whitePathList;
}