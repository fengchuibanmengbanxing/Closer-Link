package com.awake.CloserLink.admin.Remote;

import com.awake.CloserLink.admin.Common.Convention.result.Result;
import com.awake.CloserLink.admin.Remote.Req.*;
import com.awake.CloserLink.admin.Remote.Resp.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author 清醒
 * @Date 2024/5/31 20:17
 */
@FeignClient("short-link-project")
public interface OpenFeignRemoteService {
    /**
     * 创建短链接
     *
     * @param shortLinkCreatReqDTO
     * @return
     */
    @PostMapping("/api/short-link/v1/creat")
    Result<ShortLinkCreatRespDTO> creatShortLink(@RequestBody ShortLinkCreatReqDTO shortLinkCreatReqDTO);

    /**
     * 短链接分页
     *
     * @param shortLinkPageReqDTO
     * @return
     */
    @GetMapping("/api/short-link/v1/page")
    Result<Page<ShortLinkPageRespDTO>> pageShortLink(@SpringQueryMap ShortLinkPageReqDTO shortLinkPageReqDTO);

    /**
     * 分组数量和
     *
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/v1/count")
    Result<List<ShortLinkGroupCountQueryRespDTO>> countShortLink(@RequestParam("requestParam") List<String> requestParam);

    /**
     * 修改短链接
     *
     * @param shortLinkUpdateReqDTO
     */
    @PostMapping("/api/short-link/v1/update")
    void updateShortLink(@RequestBody ShortLinkUpdateReqDTO shortLinkUpdateReqDTO);

    /**
     * 获取网站标题
     *
     * @param url
     * @return
     */
    @GetMapping("/api/short-link/title")
    Result<String> getUrlTitle(@RequestParam("url") String url);

    /**
     * 保存回收站
     *
     * @param recycleBinSaveReqDTO
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    void saveRecycleBin(@RequestBody RecycleBinSaveReqDTO recycleBinSaveReqDTO);

    /**
     * 回收站分页
     * @param shortLinkPageReqDTO
     * @return
     */
    //单一链接
//    @GetMapping("/api/short-link/v1/recycle-bin/page")
//    Result<Page<ShortLinkPageRespDTO>> pageRecycleBin(@SpringQueryMap ShortLinkPageReqDTO shortLinkPageReqDTO);

//用户所有分组下的链接
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    Result<Page<ShortLinkPageRespDTO>> listPageRecycleBin(@SpringQueryMap RecycleBinPageReqDTO recycleBinPageReqDTO);

    /**
     * 恢复回收站
     *
     * @param recycleBinRecoverReqDTO
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    void recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO recycleBinRecoverReqDTO);

    /**
     * 移出回收站
     *
     * @param recycleBinRemoveReqDTO
     */
    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    void removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO recycleBinRemoveReqDTO);

    /**
     * 获取单个短链接监控数据
     *
     * @param shortLinkStatsReqDTO
     * @return
     */
    @PostMapping("/api/short-link/v1/stats")
    Result<ShortLinkStatsRespDTO> getOneStats(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 短链接信息分页
     *
     * @param shortLinkStatsAccessRecordReqDTO
     * @return
     */
    @PostMapping("/api/short-link/v1/stats/access-record")
    Result<Page<ShortLinkStatsAccessRecordRespDTO>> linkAccessRecordStats(@RequestBody ShortLinkStatsAccessRecordReqDTO shortLinkStatsAccessRecordReqDTO);

    /**
     * 分组
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/v1/stats/group")
    Result<ShortLinkStatsRespDTO> groupShortLinkStats( @SpringQueryMap ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 短链接分组监控
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/v1/stats/access-record/group")
     Result<Page<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(@SpringQueryMap ShortLinkGroupStatsAccessRecordReqDTO requestParam);

}
