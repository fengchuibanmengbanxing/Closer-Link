package com_awake_CloserLink.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_awake_CloserLink.Dto.Req.RecycleBinPageReqDTO;
import com_awake_CloserLink.Dto.Req.RecycleBinRecoverReqDTO;
import com_awake_CloserLink.Dto.Req.RecycleBinRemoveReqDTO;
import com_awake_CloserLink.Dto.Req.RecycleBinSaveReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Entitys.LinkDO;
import com_awake_CloserLink.Mapper.ShortLinkMapper;
import com_awake_CloserLink.Service.RecycleBinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com_awake_CloserLink.Common.Constant.RedisKeyConstant.GOTO_IS_NOTNULL_SHORT_LINK_KEY;
import static com_awake_CloserLink.Common.Constant.RedisKeyConstant.GOTO_SHORT_LINK_KEY;

/**
 * @Author 清醒
 * @Date 2024/5/21 19:21
 */
@Service
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper, LinkDO> implements RecycleBinService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 将链接保存回收站
     *
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
    public IPage<ShortLinkPageRespDTO> pageRecycleBin(RecycleBinPageReqDTO recycleBinPageReqDTO) {

        List<String> gidList = recycleBinPageReqDTO.getGidList();
        if (gidList != null) {
            LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                    .in(LinkDO::getGid, gidList)
                    .eq(LinkDO::getDelFlag, 0)
                    .eq(LinkDO::getEnableStatus, 0);
            IPage<LinkDO> linkDOIPage = baseMapper.selectPage(recycleBinPageReqDTO, queryWrapper);
            return linkDOIPage.convert(linkDO -> {
                ShortLinkPageRespDTO shortLinkPageRespDTO = new ShortLinkPageRespDTO();
                BeanUtil.copyProperties(linkDO, shortLinkPageRespDTO);
                return shortLinkPageRespDTO;
            });
        }
        return null;
    }

    /**
     * 恢复回收站
     *
     * @param recycleBinRecoverReqDTO gid fullLinkUrl
     */
    @Override
    public void recoverRecycleBin(RecycleBinRecoverReqDTO recycleBinRecoverReqDTO) {
        //数据库结果集updateWrapper
        LambdaUpdateWrapper<LinkDO> updateWrapper = Wrappers.lambdaUpdate(LinkDO.class)
                .eq(LinkDO::getGid, recycleBinRecoverReqDTO.getGid())
                .eq(LinkDO::getFullShortUrl, recycleBinRecoverReqDTO.getFullShortUrl())
                .eq(LinkDO::getEnableStatus, 0)
                .eq(LinkDO::getDelFlag, 0);
        //修改属性
        LinkDO linkDO = LinkDO.builder()
                .enableStatus(1)
                .build();
        baseMapper.update(linkDO, updateWrapper);
        //TODO 缓存预热 将链接缓存到redis(可不写)
        //删除redis中缓存空对象
        stringRedisTemplate.delete(String.format(GOTO_IS_NOTNULL_SHORT_LINK_KEY, recycleBinRecoverReqDTO.getFullShortUrl()));

    }

    /**
     * 从回收站删除短链接
     */
    @Override
    public void removeRecycleBin(RecycleBinRemoveReqDTO recycleBinRemoveReqDTO) {
        //数据库结果集updateWrapper
        LambdaUpdateWrapper<LinkDO> updateWrapper = Wrappers.lambdaUpdate(LinkDO.class)
                .eq(LinkDO::getGid, recycleBinRemoveReqDTO.getGid())
                .eq(LinkDO::getFullShortUrl, recycleBinRemoveReqDTO.getFullShortUrl())
                .eq(LinkDO::getEnableStatus, 1)
                .eq(LinkDO::getDelFlag, 0);
        //修改属性
        LinkDO linkDO = LinkDO.builder()
                .delFlag(1)
                .build();
        baseMapper.update(linkDO, updateWrapper);
    }


}
