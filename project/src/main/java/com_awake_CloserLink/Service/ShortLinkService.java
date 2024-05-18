package com_awake_CloserLink.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com_awake_CloserLink.Dto.Req.ShortLinkCreatReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkCreatRespDTO;
import com_awake_CloserLink.Entitys.LinkDO;

/**
 * @Author 清醒
 * @Date 2024/5/18 9:53
 */
public interface ShortLinkService extends IService<LinkDO> {
    /**
     * 创建短链接
     * @param shortLinkCreatReqDTO
     */
    ShortLinkCreatRespDTO creatShortLink(ShortLinkCreatReqDTO shortLinkCreatReqDTO);

}
