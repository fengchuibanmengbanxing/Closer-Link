package com.awake.CloserLink.project.Common.Enums;

import com.awake.CloserLink.project.Common.Convention.ErrorCode.IErrorCode;

/**
 * @Author 清醒
 * @Date 2024/5/14 10:57
 */
public enum UserErrorCodeEnum implements IErrorCode {

    USER_REGISTER_ERROR("A000100", "用户注册错误"),
    SERVICE_ERROR("B000001", "系统执行出错"),
    REMOTE_ERROR("C000001", "调用第三方服务出错"),
    IDEMPOTENT_TOKEN_NULL_ERROR("A000200", "幂等Token为空"),
    SERVICE_TIMEOUT_ERROR("B000100", "系统执行超时"),
    CLIENT_ERROR("A000001", "用户端错误"),
    USER_NAME_VERIFY_ERROR("A000110", "用户名校验失败"),
    USER_NAME_EXIST_ERROR("A000111", "用户名已存在"),
    USER_NAME_SENSITIVE_ERROR("A000112", "用户名包含敏感词"),
    USER_NAME_SPECIAL_CHARACTER_ERROR("A000113", "用户名包含特殊字符"),
    PHONE_VERIFY_ERROR("A000151", "手机格式校验失败"),
    PASSWORD_SHORT_ERROR("A000121", "密码长度不够"),
    PASSWORD_VERIFY_ERROR("A000120", "密码校验失败");
    private final String code;

    private final String message;

    UserErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }


    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
