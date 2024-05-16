package com_awake_CloserLink.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_awake_CloserLink.Entitys.GroupDO;
import com_awake_CloserLink.Mapper.GroupMapper;
import com_awake_CloserLink.Service.GroupService;
import com_awake_CloserLink.Utils.RandomUtil;
import org.springframework.stereotype.Service;

/**
 * @Author 清醒
 * @Date 2024/5/16 20:30
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    /**
     * 保存短链接组
     *
     * @param username
     */
    @Override
    public void saveGroup(String username) {
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
                .name(username).build();
        baseMapper.insert(groupDO);
    }


    //判断是否生成的gid是否重复
    private boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid);
        return baseMapper.selectOne(queryWrapper) != null;
    }

}
