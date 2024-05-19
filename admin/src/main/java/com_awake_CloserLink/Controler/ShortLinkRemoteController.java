package com_awake_CloserLink.Controler;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Remote.Req.ShortLinkCreatReqDTO;
import com_awake_CloserLink.Remote.Req.ShortLinkPageReqDTO;
import com_awake_CloserLink.Remote.Resp.ShortLinkCreatRespDTO;
import com_awake_CloserLink.Remote.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Remote.ShortLinkRemoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 清醒
 * @Date 2024/5/19 9:32
 */
@RestController
public class ShortLinkRemoteController {
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    @PostMapping("/api/short-link/admin/v1/creat")
    public Result<ShortLinkCreatRespDTO> creatShortLink(@RequestBody ShortLinkCreatReqDTO shortLinkCreatReqDTO) {
        return shortLinkRemoteService.creatShortLink(shortLinkCreatReqDTO);
    }


    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO shortLinkPageReqDTO) {
        return shortLinkRemoteService.pageShortLink(shortLinkPageReqDTO);
    }

}
