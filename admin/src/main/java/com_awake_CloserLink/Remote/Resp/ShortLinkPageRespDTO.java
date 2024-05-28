package com_awake_CloserLink.Remote.Resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author 清醒
 * @Date 2024/5/18 20:07
 */
@Data
public class ShortLinkPageRespDTO {
    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;


    /**
     * 分组标识
     */
    private String gid;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validDate;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 描述
     */
    @TableField("`describe`")
    private String describe;

    /**
     * 今日PV
     */
    private Integer todayPv;
    /**
     * 历史PV
     */
    private Integer totalPv;

    /**
     * 今日UV
     */
    private Integer todayUv;
    /**
     * 历史UV
     */
    private Integer totalUv;
    /**
     * 今日Uip
     */
    private Integer todayUip;
    /**
     * 历史Uip
     */
    private Integer totalUip;
}
