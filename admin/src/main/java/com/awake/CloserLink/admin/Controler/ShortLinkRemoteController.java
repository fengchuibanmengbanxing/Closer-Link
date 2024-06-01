package com.awake.CloserLink.admin.Controler;

import com.awake.CloserLink.admin.Common.Convention.result.Result;
import com.awake.CloserLink.admin.Common.Convention.result.Results;
import com.awake.CloserLink.admin.Remote.OpenFeignRemoteService;
import com.awake.CloserLink.admin.Remote.Req.*;
import com.awake.CloserLink.admin.Remote.Resp.*;
import com.awake.CloserLink.admin.Remote.ShortLinkRemoteService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author 清醒
 * @Date 2024/5/19 9:32
 */
@RestController
public class ShortLinkRemoteController {
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };
    @Autowired
    private OpenFeignRemoteService openFeignRemoteService;

    @PostMapping("/api/short-link/admin/v1/creat")
    public Result<ShortLinkCreatRespDTO> creatShortLink(@RequestBody ShortLinkCreatReqDTO shortLinkCreatReqDTO) {
        return openFeignRemoteService.creatShortLink(shortLinkCreatReqDTO);
    }


    @GetMapping("/api/short-link/admin/v1/page")
    public Result<Page<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO shortLinkPageReqDTO) {
        return openFeignRemoteService.pageShortLink(shortLinkPageReqDTO);
    }


    @GetMapping("/api/short-link/admin/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> countShortLink(@RequestParam("requestParam") List<String> requestParam ){
        return openFeignRemoteService.countShortLink(requestParam);
    }

    /**
     * 修改短链接
     * @param shortLinkUpdateReqDTO
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO shortLinkUpdateReqDTO){
        openFeignRemoteService.updateShortLink(shortLinkUpdateReqDTO);
        return Results.success();
    }

    /**
     *   获取网站标题
     */
    @GetMapping("/api/short-link/admin/v1/title")
    public Result<String> getUrlTitle(@RequestParam("url") String url){
        return openFeignRemoteService.getUrlTitle(url);
    }

    /**
     * 保存回收站
     * @param recycleBinSaveReqDTO
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO recycleBinSaveReqDTO) {
        openFeignRemoteService.saveRecycleBin(recycleBinSaveReqDTO);
        return Results.success();
    }

    /**
     * 回收站分页
     * @param recycleBinPageReqDTO
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<Page<ShortLinkPageRespDTO>> pageRecycleBin(RecycleBinPageReqDTO recycleBinPageReqDTO){
        return openFeignRemoteService.listPageRecycleBin(recycleBinPageReqDTO);
    }

    /**
     *恢复回收站
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/recover")
    public Result<Void>recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO recycleBinRecoverReqDTO) {
        openFeignRemoteService.recoverRecycleBin(recycleBinRecoverReqDTO);
        return Results.success();
    }

    /**
     *从回收站删除
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/remove")
    public Result<Void>removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO recycleBinRemoveReqDTO) {
        openFeignRemoteService.removeRecycleBin(recycleBinRemoveReqDTO);
        return Results.success();
    }

    /**
     * 获取监控信息
     */
    @PostMapping("/api/short-link/admin/v1/stats")
    public Result<ShortLinkStatsRespDTO> linkStats(@RequestBody ShortLinkStatsReqDTO shortLinkStatsReqDTO) {
        return openFeignRemoteService.getOneStats(shortLinkStatsReqDTO);
    }

    @PostMapping("/api/short-link/admin/v1/stats/access-record")
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> linkAccessRecordStats(@RequestBody ShortLinkStatsAccessRecordReqDTO shortLinkStatsAccessRecordReqDTO) {
        Result<Page<ShortLinkStatsAccessRecordRespDTO>> iPageResult = openFeignRemoteService.linkAccessRecordStats(shortLinkStatsAccessRecordReqDTO);
        return iPageResult;
    }

    /**
     * 短链接分组监控
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        return openFeignRemoteService.groupShortLinkStats(requestParam);
    }

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record/group")
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
        return openFeignRemoteService.groupShortLinkStatsAccessRecord(requestParam);
    }

}
