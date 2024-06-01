package com.awake.CloserLink.project.Dto.Resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 清醒
 * @Date 2024/5/18 10:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortLinkCreatRespDTO {

    private String gid;
    private String originUrl;
    private String fullShortUrl;
}
