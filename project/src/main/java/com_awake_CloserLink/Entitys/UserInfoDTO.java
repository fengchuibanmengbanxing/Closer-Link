package com_awake_CloserLink.Entitys;

/**
 * @Author 清醒
 * @Date 2024/5/16 22:28
 */
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {

    /**
     * 用户 ID
     */
    @JSONField(name = "id")
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

//    /**
//     * 用户 Token
//     */
//    private String token;
}