package com_awake_CloserLink.Remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Remote.Req.*;
import com_awake_CloserLink.Remote.Resp.ShortLinkCreatRespDTO;
import com_awake_CloserLink.Remote.Resp.ShortLinkGroupCountQueryRespDTO;
import com_awake_CloserLink.Remote.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Remote.Resp.ShortLinkStatsRespDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

/**
 * @Author 清醒
 * @Date 2024/5/19 9:05
 */
public interface ShortLinkRemoteService {


    default Result<ShortLinkCreatRespDTO> creatShortLink(@RequestBody ShortLinkCreatReqDTO shortLinkCreatReqDTO) {

        String s = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/creat", JSON.toJSONString(shortLinkCreatReqDTO));
        return JSON.parseObject(s, new TypeReference<Result<ShortLinkCreatRespDTO>>() {
        });
    }


    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO shortLinkPageReqDTO) {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("gid", shortLinkPageReqDTO.getGid());
        resultMap.put("page", shortLinkPageReqDTO.getCurrent());
        resultMap.put("size", shortLinkPageReqDTO.getSize());
        String s = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", resultMap);

        return JSON.parseObject(s, new TypeReference<Result<IPage<ShortLinkPageRespDTO>>>() {
        });
    }

    default Result<List<ShortLinkGroupCountQueryRespDTO>> countShortLink(@RequestParam("requestParam") List<String> requestParam) {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("requestParam", requestParam);
        String s = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/count", resultMap);
        return JSON.parseObject(s, new TypeReference<Result<List<ShortLinkGroupCountQueryRespDTO>>>() {
        });
    }


    default void updateShortLink(@RequestBody ShortLinkUpdateReqDTO shortLinkUpdateReqDTO) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/update", JSON.toJSONString(shortLinkUpdateReqDTO));
    }

    default Result<String> getUrlTitle(@RequestParam("url") String url) {
        String TitleString = HttpUtil.get("http://127.0.0.1:8001/api/short-link/title?url=" + url);
        return JSON.parseObject(TitleString, new TypeReference<Result<String>>() {
        });
    }

    default void saveRecycleBin(RecycleBinSaveReqDTO recycleBinSaveReqDTO) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/save", JSON.toJSONString(recycleBinSaveReqDTO));

    }

    default Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(ShortLinkPageReqDTO shortLinkPageReqDTO) {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("gid", shortLinkPageReqDTO.getGid());
        resultMap.put("page", shortLinkPageReqDTO.getCurrent());
        resultMap.put("size", shortLinkPageReqDTO.getSize());
        String s = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/page", resultMap);

        return JSON.parseObject(s, new TypeReference<Result<IPage<ShortLinkPageRespDTO>>>() {
        });
    }

    default Result<IPage<ShortLinkPageRespDTO>> listPageRecycleBin(RecycleBinPageReqDTO recycleBinPageReqDTO) {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("gidList", recycleBinPageReqDTO.getGidList());
        resultMap.put("page", recycleBinPageReqDTO.getCurrent());
        resultMap.put("size", recycleBinPageReqDTO.getSize());
        String s = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/page", resultMap);

        return JSON.parseObject(s, new TypeReference<Result<IPage<ShortLinkPageRespDTO>>>() {
        });
    }

    default void recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO recycleBinRecoverReqDTO) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/recover", JSON.toJSONString(recycleBinRecoverReqDTO));
    }


    default void removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO recycleBinRemoveReqDTO) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/remove", JSON.toJSONString(recycleBinRemoveReqDTO));
    }

    default Result<ShortLinkStatsRespDTO> getOneStats(ShortLinkStatsReqDTO shortLinkStatsReqDTO) {
        String post = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/stats", JSON.toJSONString(shortLinkStatsReqDTO));
        return JSON.parseObject(post, new TypeReference<Result<ShortLinkStatsRespDTO>>() {
        });
    }
}