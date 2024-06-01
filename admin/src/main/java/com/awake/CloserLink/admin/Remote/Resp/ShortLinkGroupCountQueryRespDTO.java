package com.awake.CloserLink.admin.Remote.Resp;

import lombok.Data;

/**
 * @Author 清醒
 * @Date 2024/5/19 11:29
 */
@Data
public class ShortLinkGroupCountQueryRespDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 短链接数量和
     */
    private Integer shortLinkCount;
}
