package com.awake.CloserLink.project.Dto.Req;

import lombok.Data;

/**
 * @Author 清醒
 * @Date 2024/5/28 14:59
 */
@Data
public class ShortLinkGroupStatsReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}