package com.awake.CloserLink.admin.Dto.Respons;

import lombok.Data;

/**
 * @Author 清醒
 * @Date 2024/5/14 20:00
 */


/**
 * 用户响应类*/
@Data
public class UserActualRespDTO {
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
