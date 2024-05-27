package com_awake_CloserLink.Dto.Resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 清醒
 * @Date 2024/5/26 14:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsOSRespDTO {

    /**
     * 统计
     */
    private Integer cnt;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 占比
     */
    private Double ratio;
}