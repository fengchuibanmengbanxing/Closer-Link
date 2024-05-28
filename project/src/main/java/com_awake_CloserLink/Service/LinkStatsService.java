package com_awake_CloserLink.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com_awake_CloserLink.Dto.Req.ShortLinkGroupStatsAccessRecordReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkGroupStatsReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkStatsAccessRecordReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkStatsReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkStatsAccessRecordRespDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkStatsRespDTO;

/**
 * @Author 清醒
 * @Date 2024/5/25 16:39
 */
public interface LinkStatsService {
    /**
     * 获取单个短链接监控信息
     * @param shortLinkStatsReqDTO
     * @return
     */
    ShortLinkStatsRespDTO getOneStats(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 获取单个短链接访客访问记录
     * @param shortLinkStatsAccessRecordReqDTO
     * @return
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> getAccessRecordStats(ShortLinkStatsAccessRecordReqDTO shortLinkStatsAccessRecordReqDTO);
    /**
     * 访问分组短链接指定时间内监控数据
     */
    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam);

    IPage<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}
