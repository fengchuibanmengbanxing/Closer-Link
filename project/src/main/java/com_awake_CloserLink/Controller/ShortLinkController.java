package com_awake_CloserLink.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Common.Convention.result.Results;
import com_awake_CloserLink.Dto.Req.ShortLinkCreatReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkPageReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkCreatRespDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Service.ShortLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author 清醒
 * @Date 2024/5/18 10:06
 */
@RestController
public class ShortLinkController {
    @Autowired
    private ShortLinkService shortLinkService;

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/admin/v1/creat")
    public Result<ShortLinkCreatRespDTO> creatShortLink(@RequestBody ShortLinkCreatReqDTO shortLinkCreatReqDTO) {
        shortLinkService.creatShortLink(shortLinkCreatReqDTO);
        return Results.success(null);
    }

    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink( ShortLinkPageReqDTO shortLinkPageReqDTO){
        return Results.success(shortLinkService.pageShortLink(shortLinkPageReqDTO));
    }

}
