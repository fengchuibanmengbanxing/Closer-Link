package com.awake.CloserLink.admin.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.awake.CloserLink.admin.Dto.Request.ShortLinkSortGroupReqDTO;
import com.awake.CloserLink.admin.Dto.Request.ShortLinkUpdateGroupReqDTO;
import com.awake.CloserLink.admin.Dto.Respons.ShortLinkGroupRespDTO;
import com.awake.CloserLink.admin.Entitys.GroupDO;

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
     * 保存默认短链接组
     * @param groupName
     */
    void saveGroup(String username,String groupName);
    /**
     *返回短链接分组集合
     */
    List<ShortLinkGroupRespDTO> listGroup();


    /**
     * 修改短链接分组信息
     */
    void updateGroup(ShortLinkUpdateGroupReqDTO shortLinkUpdateGroupReqDTO);

    /**
     * 删除短链接分组信息
     */
    void deleteGroup(String gid);
    /**
     * 排序短链接分组信息
     */
    void sortGroup(List<ShortLinkSortGroupReqDTO> shortLinkSortGroupReqDTOList);

}
