package com_awake_CloserLink.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_awake_CloserLink.Dto.Req.ShortLinkCreatReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkCreatRespDTO;
import com_awake_CloserLink.Entitys.LinkDO;
import com_awake_CloserLink.Mapper.ShortLinkMapper;
import com_awake_CloserLink.Service.ShortLinkService;
import com_awake_CloserLink.Utils.HashUtil;
import org.springframework.stereotype.Service;

/**
 * @Author 清醒
 * @Date 2024/5/18 9:53
 */
@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, LinkDO> implements ShortLinkService {
    //创建短链接
    @Override
    public ShortLinkCreatRespDTO creatShortLink(ShortLinkCreatReqDTO shortLinkCreatReqDTO) {
        //短链接后缀
        String generateSuffix = generateSuffix(shortLinkCreatReqDTO);
        LinkDO shortLinkDO = BeanUtil.toBean(shortLinkCreatReqDTO, LinkDO.class);
        //设置完整短链接
        shortLinkDO.setFullShortUrl(shortLinkDO.getDomain()+"/"+generateSuffix);
        shortLinkDO.setEnableStatus(1);
        shortLinkDO.setShortUri(generateSuffix);
        baseMapper.insert(shortLinkDO);

        //返回响应类RespDTO
        return ShortLinkCreatRespDTO.builder()
                .gid(shortLinkCreatReqDTO.getGid())
                .originUrl(shortLinkCreatReqDTO.getOriginUrl())
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .build();
    }


    //工具类实现短链接
    private String generateSuffix(ShortLinkCreatReqDTO shortLinkCreatReqDTO) {
        String originUrl = shortLinkCreatReqDTO.getOriginUrl();
        String s = HashUtil.hashToBase62(originUrl);
        return s;
    }


}
