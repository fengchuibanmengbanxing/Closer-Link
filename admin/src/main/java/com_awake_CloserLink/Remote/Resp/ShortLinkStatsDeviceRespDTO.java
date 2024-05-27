package com_awake_CloserLink.Remote.Resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 清醒
 * @Date 2024/5/26 14:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsDeviceRespDTO {

    /**
     * 统计
     */
    private Integer cnt;

    /**
     * 设备类型
     */
    private String device;

    /**
     * 占比
     */
    private Double ratio;
}
