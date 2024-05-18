package com_awake_CloserLink.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_awake_CloserLink.Common.Convention.Exception.ServiceException;
import com_awake_CloserLink.Dto.Req.ShortLinkCreatReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkPageReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkCreatRespDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Entitys.LinkDO;
import com_awake_CloserLink.Mapper.ShortLinkMapper;
import com_awake_CloserLink.Service.ShortLinkService;
import com_awake_CloserLink.Utils.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author 清醒
 * @Date 2024/5/18 9:53
 */
@Service
@Slf4j
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, LinkDO> implements ShortLinkService {
    //创建短链接布隆过滤器
    @Autowired
    private RBloomFilter<String> shortLinkCreatCachePenetrationBloomFilter;

    //创建短链接
    @Override
    public ShortLinkCreatRespDTO creatShortLink(ShortLinkCreatReqDTO shortLinkCreatReqDTO) {
        //短链接后缀
        String generateSuffix = generateSuffix(shortLinkCreatReqDTO);
        LinkDO shortLinkDO = BeanUtil.toBean(shortLinkCreatReqDTO, LinkDO.class);
        String fullShortUrl = shortLinkDO.getDomain() + "/" + generateSuffix;
        //设置完整短链接
        shortLinkDO.setFullShortUrl(fullShortUrl);
        shortLinkDO.setEnableStatus(1);
        shortLinkDO.setShortUri(generateSuffix);
        //防止数据库写入后但是布隆过滤器未添加短链接导致下次请求短链接缓存穿透
        try {
            baseMapper.insert(shortLinkDO);
        } catch (Exception e) {
            LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getFullShortUrl, fullShortUrl);
            LinkDO linkDO = baseMapper.selectOne(queryWrapper);
            if (linkDO != null) {
                log.error("数据库写入失败，请检查！");
                throw new ServiceException("短链接生成重复！");
            }
        }
        shortLinkCreatCachePenetrationBloomFilter.add(fullShortUrl);

        //返回响应类RespDTO
        //流式创建对象
        return ShortLinkCreatRespDTO.builder()
                .gid(shortLinkCreatReqDTO.getGid())
                .originUrl(shortLinkCreatReqDTO.getOriginUrl())
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .build();

    }

    //获取短链接分页

    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO shortLinkPageReqDTO) {
        String gid = shortLinkPageReqDTO.getGid();
        if (gid != null) {
            LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getGid, gid)
                    .eq(LinkDO::getDelFlag, 0)
                    .eq(LinkDO::getEnableStatus, 1);
            IPage<LinkDO> linkDOIPage = baseMapper.selectPage(shortLinkPageReqDTO, queryWrapper);
            return linkDOIPage.convert(linkDO -> {
                ShortLinkPageRespDTO shortLinkPageRespDTO = new ShortLinkPageRespDTO();
                BeanUtil.copyProperties(linkDO, shortLinkPageRespDTO);
                return shortLinkPageRespDTO;
            });
        }
        return null;
    }


    //工具类实现短链接
    private String generateSuffix(ShortLinkCreatReqDTO shortLinkCreatReqDTO) {
        int creatgenerateSuffix = 0;
        String originUrl = shortLinkCreatReqDTO.getOriginUrl();
        String shortUri;
        while (true) {
            if (creatgenerateSuffix > 10) {
                throw new ServiceException("多次创建短链接，请稍后重试！");
            }
            //相同网址可能由于毫秒数不同可以创建多个链接
            originUrl += System.currentTimeMillis();
            //该工具类只要内容不更改生成的数据仍是一样
            shortUri = HashUtil.hashToBase62(originUrl);
            //布隆过滤器不包含一定就不包含
            if (!shortLinkCreatCachePenetrationBloomFilter.contains(shortLinkCreatReqDTO.getDomain() + "/" + shortUri)) {
                break;
            }
            creatgenerateSuffix++;
        }
        return shortUri;
    }
}
