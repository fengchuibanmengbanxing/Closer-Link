package com.awake.CloserLink.admin.Dto.Respons;

import lombok.Data;

/**
 * @Author 清醒
 * @Date 2024/5/16 21:50
 */
@Data
public class ShortLinkGroupRespDTO {
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
     * 短链接数量和
     */
    private Integer shortLinkCount;

}
