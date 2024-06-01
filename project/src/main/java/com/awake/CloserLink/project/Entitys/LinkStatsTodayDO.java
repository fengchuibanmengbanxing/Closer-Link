package com.awake.CloserLink.project.Entitys;

import com.awake.CloserLink.project.Common.Database.BaseDO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author 清醒
 * @Date 2024/5/27 20:47
 */
@Data
@TableName("t_link_stats_today")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkStatsTodayDO extends BaseDO {
    /**
     * ID
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接
     */
    private String fullShortUrl;

    /**
     * 日期
     */
    private Date date;

    /**
     * PV
     */
    private Integer todayPv;

    /**
     * UV
     */
    private Integer todayUv;

    /**
     * IP
     */
    private Integer todayUip;


}
