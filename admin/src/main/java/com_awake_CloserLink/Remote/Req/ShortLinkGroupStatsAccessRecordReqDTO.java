package com_awake_CloserLink.Remote.Req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com_awake_CloserLink.Entitys.LinkAccessLogsDO;
import lombok.Data;

/**
 * @Author 清醒
 * @Date 2024/5/28 15:00
 */
@Data
public class ShortLinkGroupStatsAccessRecordReqDTO extends Page<LinkAccessLogsDO> {

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