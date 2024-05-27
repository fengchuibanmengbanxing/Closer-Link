package com_awake_CloserLink.Dto.Resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 清醒
 * @Date 2024/5/25 17:30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkStatsAccessDailyRespDTO{
    /**
     * 日期
     */
    private String date;

    /**
     * 访问量
     */
    private Integer pv;

    /**
     * 独立访客数
     */
    private Integer uv;

    /**
     * 独立IP数
     */
    private Integer uip;


}
