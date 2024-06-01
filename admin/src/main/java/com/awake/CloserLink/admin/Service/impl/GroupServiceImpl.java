package com.awake.CloserLink.admin.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.awake.CloserLink.admin.Common.Constant.RedisCacheConstant;
import com.awake.CloserLink.admin.Remote.OpenFeignRemoteService;
import com.awake.CloserLink.admin.Utils.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.awake.CloserLink.admin.Common.Convention.Exception.ClientException;
import com.awake.CloserLink.admin.Common.Convention.result.Result;
import com.awake.CloserLink.admin.Dto.Request.ShortLinkSortGroupReqDTO;
import com.awake.CloserLink.admin.Dto.Request.ShortLinkUpdateGroupReqDTO;
import com.awake.CloserLink.admin.Entitys.GroupDO;
import com.awake.CloserLink.admin.Mapper.GroupMapper;
import com.awake.CloserLink.admin.Remote.Resp.ShortLinkGroupCountQueryRespDTO;
import com.awake.CloserLink.admin.Service.GroupService;
import com.awake.CloserLink.admin.Common.Biz.UserContext;
import com.awake.CloserLink.admin.Dto.Respons.ShortLinkGroupRespDTO;
import com.awake.CloserLink.admin.Remote.ShortLinkRemoteService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author 清醒
 * @Date 2024/5/16 20:30
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };
    @Autowired
    private OpenFeignRemoteService openFeignRemoteService;
    @Autowired
    private RedissonClient redissonClient;

    @Value("short-link.group.size")
    private String groupSize;


    /**
     * 保存短链接组
     *
     * @param groupName
     */
    //登陆后创建分组
    @Override
    public void saveGroup(String groupName) {
        saveGroup(UserContext.getUsername(), groupName);
    }

    //注册后默认创建分组
    @Override
    public void saveGroup(String username, String groupName) {
        RLock lock = redissonClient.getLock(String.format(RedisCacheConstant.LOCK_GROUP_CREATE_KEY, username));
        lock.lock();
        //用户分组创建锁（防止多端登录创建）
        try {
            LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getUsername, username);
            List<GroupDO> groupDOS = baseMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(groupDOS) && groupDOS.size() == Integer.parseInt(groupSize)) {
                throw new ClientException("用户分组上限！");
            }
            /**
             * 生成随机六位数据组id
             */
            String gid = RandomUtil.generateRandomCode();
            while (true) {
                if (!hasGid(username, gid)) {
                    break;
                }
            }
            GroupDO groupDO = GroupDO.builder()
                    .gid(gid)
                    .name(groupName)
                    .username(username)
                    .sortOrder(0)
                    .build();
            baseMapper.insert(groupDO);
        } finally {
            lock.unlock();
        }

    }

    //根据用户名获取短链接集合
    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        //查询当前用户的所有分组
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOS = baseMapper.selectList(queryWrapper);
        // 遍历集合，将集合中的数据转换为响应对象
        //获取所有分组标识gid
        List<String> strings = groupDOS.stream().map(GroupDO::getGid).toList();
        //远程调用查询所有gid分组集合短链接数量
        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = openFeignRemoteService.countShortLink(strings);
        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOList = BeanUtil.copyToList(groupDOS, ShortLinkGroupRespDTO.class);
        //遍历所有分组
        shortLinkGroupRespDTOList.forEach(each -> {
            //过滤出远程调用的存在的分组标识gid
            Optional<ShortLinkGroupCountQueryRespDTO> first
                    = listResult.getData().stream().filter(item -> Objects.equals(item.getGid(), each.getGid()))
                    .findFirst();
            //设置每个分组短链接数量
            first.ifPresent(item -> each.setShortLinkCount(first.get().getShortLinkCount()));
        });
        return shortLinkGroupRespDTOList;
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
    private boolean hasGid(String username, String gid) {
        //TODO 获取用户名
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getName, Optional.ofNullable(username).orElse(UserContext.getUsername()));
        return baseMapper.selectOne(queryWrapper) != null;
    }


}
