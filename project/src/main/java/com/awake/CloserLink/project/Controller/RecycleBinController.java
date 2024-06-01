package com.awake.CloserLink.project.Controller;

import com.awake.CloserLink.project.Dto.Req.RecycleBinPageReqDTO;
import com.awake.CloserLink.project.Dto.Req.RecycleBinRecoverReqDTO;
import com.awake.CloserLink.project.Dto.Req.RecycleBinRemoveReqDTO;
import com.awake.CloserLink.project.Dto.Req.RecycleBinSaveReqDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.awake.CloserLink.project.Common.Convention.result.Result;
import com.awake.CloserLink.project.Common.Convention.result.Results;
import com.awake.CloserLink.project.Dto.Resp.ShortLinkPageRespDTO;
import com.awake.CloserLink.project.Service.RecycleBinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 清醒
 * @Date 2024/5/21 19:19
 */
@RestController
public class RecycleBinController {
    @Autowired
    private RecycleBinService recycleBinService;

    /**
     * 回收站保存
     * @param recycleBinSaveReqDTO
     * @return
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO recycleBinSaveReqDTO) {
        recycleBinService.saveRecycleBin(recycleBinSaveReqDTO);
        return Results.success();
    }

    /**
     * 回收站分页
     */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(RecycleBinPageReqDTO recycleBinPageReqDTO){
        return Results.success(recycleBinService.listPageRecycleBin(recycleBinPageReqDTO));
    }

    /**
     *恢复回收站
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void>recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO recycleBinRecoverReqDTO) {
        recycleBinService.recoverRecycleBin(recycleBinRecoverReqDTO);
        return Results.success();
    }

    /**
     *从回收站删除
     */
    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    public Result<Void>removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO recycleBinRemoveReqDTO) {
        recycleBinService.removeRecycleBin(recycleBinRemoveReqDTO);
        return Results.success();
    }

}
