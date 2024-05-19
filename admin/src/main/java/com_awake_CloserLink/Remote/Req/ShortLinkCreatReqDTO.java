package com_awake_CloserLink.Remote.Req;

import lombok.Data;

import java.util.Date;

/**
 * @Author 清醒
 * @Date 2024/5/18 10:17
 */
@Data
public class ShortLinkCreatReqDTO {


    /**
     * 域名
     */
    private String domain;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 分组标识
     */
    private String gid;


    /**
     * 创建类型 0：控制台 1：接口
     */
    private Integer createdType;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    private Date validDate;

    /**
     * 描述
     */
    private String describe;

}
