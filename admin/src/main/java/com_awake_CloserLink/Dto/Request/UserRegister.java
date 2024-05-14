package com_awake_CloserLink.Dto.Request;

import lombok.Data;

/**
 * @Author 清醒
 * @Date 2024/5/14 22:33
 */
@Data
public class UserRegister {
    /**
     * ID
     */
    private String id;

    /**
     * 用户名
     */
    private String username;


    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */

    private String phone;

    /**
     * 邮箱
     */
    private String mail;
}
