package com_awake_CloserLink.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com_awake_CloserLink.Dto.Request.ShortLinkUpdateGroupReqDTO;
import com_awake_CloserLink.Dto.Respons.ShortLinkGroupRespDTO;
import com_awake_CloserLink.Entitys.GroupDO;

import java.util.List;

/**
 * @Author 清醒
 * @Date 2024/5/16 20:29
 */
public interface GroupService extends IService<GroupDO> {
    /**
     * 保存短链接组
     * @param groupName
     */
    void saveGroup(String groupName);

    /**
     *返回短链接分组集合
     */
    List<ShortLinkGroupRespDTO> listGroup();


    /**
     * 修改短链接分组信息
     */
    void updateGroup(ShortLinkUpdateGroupReqDTO shortLinkUpdateGroupReqDTO);


}
