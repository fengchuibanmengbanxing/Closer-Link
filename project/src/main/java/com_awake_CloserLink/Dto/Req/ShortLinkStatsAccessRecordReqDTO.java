package com_awake_CloserLink.Dto.Req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com_awake_CloserLink.Entitys.LinkAccessLogsDO;
import lombok.Data;

/**
 * @Author 清醒
 * @Date 2024/5/27 9:48
 */
@Data
public class ShortLinkStatsAccessRecordReqDTO extends Page<LinkAccessLogsDO> {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

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

    /**
     * 启用标识 0：启用 1：未启用
     */
    private Integer enableStatus;
}