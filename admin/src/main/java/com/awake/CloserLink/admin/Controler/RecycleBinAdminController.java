package com.awake.CloserLink.admin.Controler;

import com.awake.CloserLink.admin.Common.Convention.result.Result;
import com.awake.CloserLink.admin.Remote.Req.RecycleBinPageReqDTO;
import com.awake.CloserLink.admin.Remote.Resp.ShortLinkPageRespDTO;
import com.awake.CloserLink.admin.Service.RecycleBinAdminService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    public Result<Page<ShortLinkPageRespDTO>> listPageRecycleBin(RecycleBinPageReqDTO recycleBinPageReqDTO){
        Result<Page<ShortLinkPageRespDTO>> pageRespDTOIPage= recycleBinAdminService.listPageRecycleBin(recycleBinPageReqDTO);
        return pageRespDTOIPage;
    }

}
