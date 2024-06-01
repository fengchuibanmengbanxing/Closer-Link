package com.awake.CloserLink.project.Dto.Req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 清醒
 * @Date 2024/5/22 18:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecycleBinRecoverReqDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 完整链
     */
    private String fullShortUrl;
}
