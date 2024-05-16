package com_awake_CloserLink.Entitys;

import lombok.Data;

/**
 * @Author 清醒
 * @Date 2024/5/16 20:28
 */
@Data
public class GroupDO {
    /**
     * ID
     */
    private String id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private String sortOrder;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 删除标识 0：未删除 1：已删除
     */
    private String delFlag;
}
