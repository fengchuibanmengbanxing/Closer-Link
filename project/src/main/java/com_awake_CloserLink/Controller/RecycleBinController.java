package com_awake_CloserLink.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Common.Convention.result.Results;
import com_awake_CloserLink.Dto.Req.RecycleBinSaveReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkPageReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Service.RecycleBinService;
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
     * @param shortLinkPageReqDTO 分组标识
     * @return
     */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(ShortLinkPageReqDTO shortLinkPageReqDTO){
        return Results.success(recycleBinService.pageRecycleBin(shortLinkPageReqDTO));
    }
}
