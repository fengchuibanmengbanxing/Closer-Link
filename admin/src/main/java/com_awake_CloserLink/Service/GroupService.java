package com_awake_CloserLink.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com_awake_CloserLink.Entitys.GroupDO;

/**
 * @Author 清醒
 * @Date 2024/5/16 20:29
 */
public interface GroupService extends IService<GroupDO> {
    /**
     * 保存短链接组
     * @param username
     */
    void saveGroup(String username);
}
