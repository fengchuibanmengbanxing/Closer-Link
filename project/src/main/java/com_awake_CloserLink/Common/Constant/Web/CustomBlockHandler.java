package com_awake_CloserLink.Common.Constant.Web;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Dto.Req.ShortLinkCreatReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkCreatRespDTO;

/**
 * @Author 清醒
 * @Date 2024/5/30 14:07
 */
public class CustomBlockHandler {

    public static Result<ShortLinkCreatRespDTO> createShortLinkBlockHandlerMethod(ShortLinkCreatReqDTO requestParam, BlockException exception) {
        return new Result<ShortLinkCreatRespDTO>().setCode("B100000").setMessage("当前访问网站人数过多，请稍后再试...");
    }
}