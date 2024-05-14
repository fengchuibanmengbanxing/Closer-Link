package com_awake_CloserLink.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com_awake_CloserLink.Dto.Respons.UserRespDTO;
import com_awake_CloserLink.Entitys.UserDO;

/**
 * @Author 清醒
 * @Date 2024/5/14 9:52
 */
public interface UserService extends IService<UserDO> {
    //根据用户名查询用户信息
    UserRespDTO getUsernameByUserName(String username);
}
