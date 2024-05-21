package com_awake_CloserLink.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_awake_CloserLink.Dto.Req.RecycleBinSaveReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkPageReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Entitys.LinkDO;
import com_awake_CloserLink.Mapper.ShortLinkMapper;
import com_awake_CloserLink.Service.RecycleBinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com_awake_CloserLink.Common.Constant.RedisKeyConstant.GOTO_SHORT_LINK_KEY;

/**
 * @Author 清醒
 * @Date 2024/5/21 19:21
 */
@Service
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper,LinkDO> implements RecycleBinService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 将链接保存回收站
     * @param recycleBinSaveReqDTO
     */
    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO recycleBinSaveReqDTO) {
        LambdaUpdateWrapper<LinkDO> updateWrapper = Wrappers.lambdaUpdate(LinkDO.class)
                .eq(LinkDO::getGid, recycleBinSaveReqDTO.getGid())
                .eq(LinkDO::getFullShortUrl, recycleBinSaveReqDTO.getFullShortUrl())
                .eq(LinkDO::getEnableStatus, 1)
                .eq(LinkDO::getDelFlag, 0);
        LinkDO linkDO = LinkDO.builder()
                .enableStatus(0)
                .build();
        baseMapper.update(linkDO, updateWrapper);
        //删除redis中缓存
        stringRedisTemplate.delete(String.format(GOTO_SHORT_LINK_KEY, recycleBinSaveReqDTO.getFullShortUrl()));

    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageRecycleBin(ShortLinkPageReqDTO shortLinkPageReqDTO) {

        String gid = shortLinkPageReqDTO.getGid();
        if (gid != null) {
            LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getGid, gid)
                    .eq(LinkDO::getDelFlag, 0)
                    .eq(LinkDO::getEnableStatus, 0);
            IPage<LinkDO> linkDOIPage = baseMapper.selectPage(shortLinkPageReqDTO, queryWrapper);
            return linkDOIPage.convert(linkDO -> {
                ShortLinkPageRespDTO shortLinkPageRespDTO = new ShortLinkPageRespDTO();
                BeanUtil.copyProperties(linkDO, shortLinkPageRespDTO);
                return shortLinkPageRespDTO;
            });
        }
        return null;
    }
}
