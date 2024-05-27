package com_awake_CloserLink.Entitys;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 清醒
 * @Date 2024/5/24 20:19
 */
@Data
@TableName("t_link_access_logs")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkAccessLogsDO {
    /**
     * ID
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 用户信息
     */
    private String user;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * IP
     */
    private String ip;

    /**
     * 访问设备
     */
    private String device;

    /**
     * 访问地区
     */
    private String locale;
    /**
     * 访问网络
     */
    private String network;
}
