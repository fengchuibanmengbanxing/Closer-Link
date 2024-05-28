package com_awake_CloserLink.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Common.Convention.result.Results;
import com_awake_CloserLink.Dto.Req.ShortLinkStatsAccessRecordReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkStatsReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkStatsAccessRecordRespDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkStatsRespDTO;
import com_awake_CloserLink.Service.LinkStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 清醒
 * @Date 2024/5/25 16:34
 */
@RestController
public class LinkStatsController {
    @Autowired
    private LinkStatsService linkStatsService;

    @PostMapping("/api/short-link/v1/stats")
    public Result<ShortLinkStatsRespDTO> linkStats(@RequestBody ShortLinkStatsReqDTO shortLinkStatsReqDTO) {
        ShortLinkStatsRespDTO shortLinkStatsRespDTO=linkStatsService.getOneStats(shortLinkStatsReqDTO);
        return Results.success(shortLinkStatsRespDTO);
    }

    /**
     * 单一短链接监控
     * @param shortLinkStatsAccessRecordReqDTO
     * @return
     */
    @PostMapping("/api/short-link/v1/stats/access-record")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> linkAccessRecordStats(@RequestBody ShortLinkStatsAccessRecordReqDTO shortLinkStatsAccessRecordReqDTO) {
        return Results.success(linkStatsService.getAccessRecordStats(shortLinkStatsAccessRecordReqDTO));
    }


}
