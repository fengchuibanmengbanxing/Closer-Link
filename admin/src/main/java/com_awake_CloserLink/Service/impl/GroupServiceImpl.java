package com_awake_CloserLink.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_awake_CloserLink.Common.Biz.UserContext;
import com_awake_CloserLink.Dto.Respons.ShortLinkGroupRespDTO;
import com_awake_CloserLink.Entitys.GroupDO;
import com_awake_CloserLink.Mapper.GroupMapper;
import com_awake_CloserLink.Service.GroupService;
import com_awake_CloserLink.Utils.RandomUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author 清醒
 * @Date 2024/5/16 20:30
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    /**
     * 保存短链接组
     *
     * @param groupName
     */
    @Override
    public void saveGroup(String groupName) {
        /**
         * 生成随机六位数据组id
         */
        String gid = RandomUtil.generateRandomCode();
        while (true) {
            if (!hasGid(gid)) {
                break;
            }
        }
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(UserContext.getUsername())
                .sortOrder(0)
                .build();
        baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag,0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder,GroupDO::getUpdateTime);
        List<GroupDO> groupDOS = baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(groupDOS, ShortLinkGroupRespDTO.class);
    }


    //判断是否生成的gid是否重复
    private boolean hasGid(String gid) {
        //TODO 获取用户名
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getName,UserContext.getUsername());
        return baseMapper.selectOne(queryWrapper) != null;
    }

}
