package com.awake.CloserLink.admin.Dto.Respons;

/**
 * @Author 清醒
 * @Date 2024/5/14 10:01
 */

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.awake.CloserLink.admin.Common.Serializ.PhoneDesensitizationSerializer;
import lombok.Data;

/**
 * 用户响应类*/
@Data
public class UserRespDTO {
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
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * 邮箱
     */
    private String mail;
}
