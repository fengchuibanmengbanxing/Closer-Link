package com.awake.CloserLink.admin.Service.impl;

import com.awake.CloserLink.admin.Common.Biz.UserContext;
import com.awake.CloserLink.admin.Common.Convention.result.Result;
import com.awake.CloserLink.admin.Entitys.GroupDO;
import com.awake.CloserLink.admin.Mapper.GroupMapper;
import com.awake.CloserLink.admin.Remote.OpenFeignRemoteService;
import com.awake.CloserLink.admin.Remote.Req.RecycleBinPageReqDTO;
import com.awake.CloserLink.admin.Remote.Resp.ShortLinkPageRespDTO;
import com.awake.CloserLink.admin.Remote.ShortLinkRemoteService;
import com.awake.CloserLink.admin.Service.RecycleBinAdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author 清醒
 * @Date 2024/5/21 22:25
 */
@Service
public class RecycleBinAdminServiceImpl implements RecycleBinAdminService {
    @Autowired
    private GroupMapper groupMapper;
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };
    @Autowired
    private OpenFeignRemoteService openFeignRemoteService;
    @Override
    public Result<Page<ShortLinkPageRespDTO>> listPageRecycleBin(RecycleBinPageReqDTO recycleBinPageReqDTO) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getName, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);
        List<String> gidList = groupDOList.stream().map(groupDO -> groupDO.getGid()).toList();
        recycleBinPageReqDTO.setGidList(gidList);
        Result<Page<ShortLinkPageRespDTO>> iPageResult = openFeignRemoteService.listPageRecycleBin(recycleBinPageReqDTO);
        return iPageResult;
    }
}
