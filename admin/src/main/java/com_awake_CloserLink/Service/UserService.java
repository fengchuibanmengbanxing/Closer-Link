package com_awake_CloserLink.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com_awake_CloserLink.Dto.Request.UserLoginReqDTO;
import com_awake_CloserLink.Dto.Request.UserRegister;
import com_awake_CloserLink.Dto.Request.UserUpdateReqDTO;
import com_awake_CloserLink.Dto.Respons.UserLoginRespDTO;
import com_awake_CloserLink.Dto.Respons.UserRespDTO;
import com_awake_CloserLink.Entitys.UserDO;

/**
 * @Author 清醒
 * @Date 2024/5/14 9:52
 */
public interface UserService extends IService<UserDO> {
    //根据用户名查询用户信息
    UserRespDTO getUsernameByUserName(String username);

    /**
     * 判断用户名是否存在
     *
     * @param username
     * @return
     */
    Boolean isHasUsername(String username);

    void register(UserRegister UserRegister);

    int update_user(UserUpdateReqDTO userUpdateReqDTO);

    /**
     * 用户登录
     * 用户名及密码
     * @return
     */
    UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO);
    /**
     * 校验用户登录
     * 用户名及密码
     * @return
     */
    Boolean checkLogin(String token,String username);

    /**
     * 判断用户名是否存在
     *@param token 用户登录uuid
     * @param username 用户名
     * @return
     */
    void logout(String token, String username);
}
