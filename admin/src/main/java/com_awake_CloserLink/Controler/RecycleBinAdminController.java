package com_awake_CloserLink.Controler;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Remote.Req.RecycleBinPageReqDTO;
import com_awake_CloserLink.Remote.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Service.RecycleBinAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 清醒
 * @Date 2024/5/21 22:19
 */
@RestController
public class RecycleBinAdminController {

    @Autowired
    private RecycleBinAdminService recycleBinAdminService;

    /**
     *回收站分页查询
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page-list")
    public Result<IPage<ShortLinkPageRespDTO>> listPageRecycleBin(RecycleBinPageReqDTO recycleBinPageReqDTO){
        Result<IPage<ShortLinkPageRespDTO>> pageRespDTOIPage= recycleBinAdminService.listPageRecycleBin(recycleBinPageReqDTO);
        return pageRespDTOIPage;
    }

}
