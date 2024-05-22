package com_awake_CloserLink.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com_awake_CloserLink.Common.Biz.UserContext;
import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Entitys.GroupDO;
import com_awake_CloserLink.Mapper.GroupMapper;
import com_awake_CloserLink.Remote.Req.RecycleBinPageReqDTO;
import com_awake_CloserLink.Remote.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Remote.ShortLinkRemoteService;
import com_awake_CloserLink.Service.RecycleBinAdminService;
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
    @Override
    public Result<IPage<ShortLinkPageRespDTO>> listPageRecycleBin(RecycleBinPageReqDTO recycleBinPageReqDTO) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getName, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);
        List<String> gidList = groupDOList.stream().map(groupDO -> groupDO.getGid()).toList();
        recycleBinPageReqDTO.setGidList(gidList);
        Result<IPage<ShortLinkPageRespDTO>> iPageResult = shortLinkRemoteService.listPageRecycleBin(recycleBinPageReqDTO);
        return iPageResult;
    }
}
