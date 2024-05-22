package com_awake_CloserLink.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Remote.Req.RecycleBinPageReqDTO;
import com_awake_CloserLink.Remote.Resp.ShortLinkPageRespDTO;

/**
 * @Author 清醒
 * @Date 2024/5/21 22:25
 */
public interface RecycleBinAdminService  {
    /**
     * 查询用户所有分组回收站短链接
     * @param recycleBinPageReqDTO
     * @return
     */
    Result<IPage<ShortLinkPageRespDTO>> listPageRecycleBin(RecycleBinPageReqDTO recycleBinPageReqDTO);
}
