package com_awake_CloserLink.Controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com_awake_CloserLink.Common.Constant.Web.CustomBlockHandler;
import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Common.Convention.result.Results;
import com_awake_CloserLink.Dto.Req.ShortLinkCreatReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkPageReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkUpdateReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkCreatRespDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkGroupCountQueryRespDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Service.ShortLinkService;
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
    public Result<IPage<ShortLinkPageRespDTO>> pageLink( ShortLinkPageReqDTO shortLinkPageReqDTO){
        return Results.success(shortLinkService.pageLink(shortLinkPageReqDTO));
    }

    /**
     * 短链接分组集合
     * @param RequestParam
     * @return
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> countShortLink(@RequestParam("requestParam") List<String> RequestParam ){
        return Results.success(shortLinkService.countShortLink(RequestParam));
    }

    /**
     * 修改短链接信息
     * @param shortLinkUpdateReqDTO
     * @return
     */
    @PostMapping("/api/short-link/v1/update")
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
    public void restoreUrl(@PathVariable ("short-link") String shortLink, ServletResponse response, ServletRequest request) throws IOException {
        shortLinkService.restoreUrl(shortLink,request,response);
    }

//    /**
//     * 分页查询短链接
//
//     * @throws IOException
//     */
//    @PostMapping("/api/short-link/v1/update")
//    public Result<IPage<ShortLinkPageRespDTO>> pageLink(@RequestBody ShortLinkPageReqDTO shortLinkPageReqDTO )  {
//        return Results.success(shortLinkService.pageLink(shortLinkPageReqDTO));
//    }

}
