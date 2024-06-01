package com.awake.CloserLink.admin.Common.Convention.result;

/**
 * @Author 清醒
 * @Date 2024/5/14 10:35
 */
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 全局返回对象
 */
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 5679018624309023727L;

    /**
     * 正确返回码
     */
    public static final String SUCCESS_CODE = "0";

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 请求ID
     */
    private String requestId;

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }

    // 返回数据
//    public static <T> Result<T> build(T body, Integer code, String message) {
//        Result<T> result = new Result<>();
//        result.setData(body);
//        result.setCode(code);
//        result.setMessage(message);
//        return result;
//    }
//
//    // 通过枚举构造Result对象
//    public static <T> Result build(T body , ResultCodeEnum resultCodeEnum) {
//        return build(body , resultCodeEnum.getCode() , resultCodeEnum.getMessage()) ;
//    }
}