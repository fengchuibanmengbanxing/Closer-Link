package com_awake_CloserLink.Entitys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com_awake_CloserLink.Common.Database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author 清醒
 * @Date 2024/5/18 9:49
 */

/**
 * 短链接实体
 */
@Data
@TableName("t_link")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkDO extends BaseDO {


    /**
     * ID
     */
    private Long id;

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
     * 点击量
     */
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 启用标识 0：未启用 1：已启用
     */
    private Integer enableStatus;

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
     * 删除
     */
    private Integer delFlag;

    /**
     * 描述
     */
    @TableField("`describe`")
    private String describe;
    /**
     * 图标
     */
    private String favicon;

}
