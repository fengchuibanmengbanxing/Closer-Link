package com.awake.CloserLink.project.Service;

import com.awake.CloserLink.project.Dto.Req.RecycleBinPageReqDTO;
import com.awake.CloserLink.project.Dto.Req.RecycleBinRecoverReqDTO;
import com.awake.CloserLink.project.Dto.Req.RecycleBinRemoveReqDTO;
import com.awake.CloserLink.project.Dto.Req.RecycleBinSaveReqDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.awake.CloserLink.project.Dto.Resp.ShortLinkPageRespDTO;
import com.awake.CloserLink.project.Entitys.LinkDO;

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
     * @return
     */
    IPage<ShortLinkPageRespDTO> listPageRecycleBin(RecycleBinPageReqDTO RecycleBinPageReqDTO);

    /**
     * 恢复回收站
     */
    void recoverRecycleBin(RecycleBinRecoverReqDTO recycleBinRecoverReqDTO);

    /**
     *从回收站删除
     */
    void removeRecycleBin(RecycleBinRemoveReqDTO recycleBinRemoveReqDTO);
}
