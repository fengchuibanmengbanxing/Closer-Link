package com_awake_CloserLink.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_awake_CloserLink.Common.Biz.UserContext;
import com_awake_CloserLink.Dto.Request.ShortLinkSortGroupReqDTO;
import com_awake_CloserLink.Dto.Request.ShortLinkUpdateGroupReqDTO;
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
                .name(groupName)
                .username(UserContext.getUsername())
                .sortOrder(0)
                .build();
        baseMapper.insert(groupDO);
    }

    //根据用户名获取短链接集合
    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOS = baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(groupDOS, ShortLinkGroupRespDTO.class);
    }


    //修改短链接分组用户名
    @Override
    public void updateGroup(ShortLinkUpdateGroupReqDTO shortLinkUpdateGroupReqDTO) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, shortLinkUpdateGroupReqDTO.getGid())
                .eq(GroupDO::getDelFlag, 0);

        GroupDO groupDO = new GroupDO();
        groupDO.setName(shortLinkUpdateGroupReqDTO.getGroupName());
        baseMapper.update(groupDO, updateWrapper);
    }


    //删除短链接分组信息
    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO, updateWrapper);
    }

    //对分组信息排序
    @Override
    public void sortGroup(List<ShortLinkSortGroupReqDTO> shortLinkSortGroupReqDTOList) {
        shortLinkSortGroupReqDTOList.forEach(shortLinkSortGroupReqDTO -> {
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getGid, shortLinkSortGroupReqDTO.getGid())
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getDelFlag, 0);
            GroupDO groupDO = new GroupDO();
            groupDO.setSortOrder(shortLinkSortGroupReqDTO.getSortOrder());
            baseMapper.update(groupDO, updateWrapper);
        });
    }


    //判断是否生成的gid是否重复
    private boolean hasGid(String gid) {
        //TODO 获取用户名
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getName, UserContext.getUsername());
        return baseMapper.selectOne(queryWrapper) != null;
    }


}
