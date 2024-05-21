package com_awake_CloserLink.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com_awake_CloserLink.Dto.Req.RecycleBinSaveReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkPageReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Entitys.LinkDO;

/**
 * @Author 清醒
 * @Date 2024/5/21 19:20
 */
public interface RecycleBinService extends IService<LinkDO> {
    /**
     * 将链接保存回收站
     * @param recycleBinSaveReqDTO
     */
    void saveRecycleBin(RecycleBinSaveReqDTO recycleBinSaveReqDTO);

    /**
     * 分页回收站
     * @param shortLinkPageReqDTO
     * @return
     */
    IPage<ShortLinkPageRespDTO> pageRecycleBin(ShortLinkPageReqDTO shortLinkPageReqDTO);
}
