package com.awake.CloserLink.admin.Service;

import com.awake.CloserLink.admin.Common.Convention.result.Result;
import com.awake.CloserLink.admin.Remote.Req.RecycleBinPageReqDTO;
import com.awake.CloserLink.admin.Remote.Resp.ShortLinkPageRespDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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
    Result<Page<ShortLinkPageRespDTO>> listPageRecycleBin(RecycleBinPageReqDTO recycleBinPageReqDTO);
}
