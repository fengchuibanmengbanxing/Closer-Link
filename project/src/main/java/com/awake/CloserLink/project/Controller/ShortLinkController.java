package com.awake.CloserLink.project.Controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.awake.CloserLink.project.Dto.Req.ShortLinkCreatReqDTO;
import com.awake.CloserLink.project.Dto.Req.ShortLinkPageReqDTO;
import com.awake.CloserLink.project.Dto.Req.ShortLinkUpdateReqDTO;
import com.awake.CloserLink.project.Dto.Resp.ShortLinkCreatRespDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.awake.CloserLink.project.Common.Constant.Web.CustomBlockHandler;
import com.awake.CloserLink.project.Common.Convention.result.Result;
import com.awake.CloserLink.project.Common.Convention.result.Results;
import com.awake.CloserLink.project.Dto.Resp.ShortLinkGroupCountQueryRespDTO;
import com.awake.CloserLink.project.Dto.Resp.ShortLinkPageRespDTO;
import com.awake.CloserLink.project.Service.ShortLinkService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
    @PostMapping("/api/short-link/v1/creat")
    @SentinelResource(
            value = "create_short-link",
            blockHandler = "createShortLinkBlockHandlerMethod",
            blockHandlerClass = CustomBlockHandler.class
    )
    public Result<ShortLinkCreatRespDTO> creatShortLink(@RequestBody ShortLinkCreatReqDTO shortLinkCreatReqDTO) {
        shortLinkService.creatShortLink(shortLinkCreatReqDTO);
        return Results.success(null);
    }

    /**
     * 短板链接分页查询
     * @param shortLinkPageReqDTO
     * @return
     */
    @GetMapping("/api/short-link/v1/page")
    @SentinelResource(
            value = "create_short-link",
            blockHandler = "createShortLinkBlockHandlerMethod",
            blockHandlerClass = CustomBlockHandler.class
    )
    public Result<IPage<ShortLinkPageRespDTO>> pageLink( ShortLinkPageReqDTO shortLinkPageReqDTO){
        return Results.success(shortLinkService.pageLink(shortLinkPageReqDTO));
    }

    /**
     * 短链接分组集合
     * @param RequestParam
     * @return
     */
    @GetMapping("/api/short-link/v1/count")
    @SentinelResource(
            value = "create_short-link",
            blockHandler = "createShortLinkBlockHandlerMethod",
            blockHandlerClass = CustomBlockHandler.class
    )
    public Result<List<ShortLinkGroupCountQueryRespDTO>> countShortLink(@RequestParam("requestParam") List<String> RequestParam ){
        return Results.success(shortLinkService.countShortLink(RequestParam));
    }

    /**
     * 修改短链接信息
     * @param shortLinkUpdateReqDTO
     * @return
     */
    @PostMapping("/api/short-link/v1/update")
    @SentinelResource(
            value = "create_short-link",
            blockHandler = "createShortLinkBlockHandlerMethod",
            blockHandlerClass = CustomBlockHandler.class
    )
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO shortLinkUpdateReqDTO){
        shortLinkService.updateShortLink(shortLinkUpdateReqDTO);
        return Results.success();
    }

    /**
     * 短链接跳转
     * @param shortLink 短链接
     * @param response  响应
     * @param request   请求
     * @throws IOException
     */
    @GetMapping("/{short-link}")
    @SentinelResource(
            value = "create_short-link",
            blockHandler = "createShortLinkBlockHandlerMethod",
            blockHandlerClass = CustomBlockHandler.class
    )
    public void restoreUrl(@PathVariable ("short-link") String shortLink, ServletResponse response, ServletRequest request) throws IOException {
        shortLinkService.restoreUrl(shortLink,request,response);
    }

}
