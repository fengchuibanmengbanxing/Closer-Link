package com_awake_CloserLink.Remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Remote.Req.ShortLinkCreatReqDTO;
import com_awake_CloserLink.Remote.Req.ShortLinkPageReqDTO;
import com_awake_CloserLink.Remote.Resp.ShortLinkCreatRespDTO;
import com_awake_CloserLink.Remote.Resp.ShortLinkGroupCountQueryRespDTO;
import com_awake_CloserLink.Remote.Resp.ShortLinkPageRespDTO;
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

        String s = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/creat" ,JSON.toJSONString(shortLinkCreatReqDTO));
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

    default Result<List<ShortLinkGroupCountQueryRespDTO>> countShortLink(@RequestParam("requestParam") List<String> requestParam ){
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("requestParam",requestParam);
        String s = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/count", resultMap);
        return JSON.parseObject(s, new TypeReference<Result<List<ShortLinkGroupCountQueryRespDTO>>>() {
        });
    }
}
