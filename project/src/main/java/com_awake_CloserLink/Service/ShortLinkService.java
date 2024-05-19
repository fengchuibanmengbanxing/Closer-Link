package com_awake_CloserLink.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com_awake_CloserLink.Dto.Req.ShortLinkCreatReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkPageReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkCreatRespDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkGroupCountQueryRespDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Entitys.LinkDO;

import java.util.List;

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

    /**
     * 获取短链接分页
     * @param shortLinkPageReqDTO
     * @return
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO shortLinkPageReqDTO);
    /**
     * 获取分组短链接总和
     * @param requestParam
     * @return
     */
    List<ShortLinkGroupCountQueryRespDTO>countShortLink(List<String> requestParam);

}
