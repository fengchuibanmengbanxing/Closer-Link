package com_awake_CloserLink.Common.Enums;

/**
 * @Author 清醒
 * @Date 2024/5/19 21:25
 */
import lombok.Getter;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public enum VailDateTypeEnum {

    /**
     * 永久有效期
     */
    PERMANENT(0),

    /**
     * 自定义有效期
     */
    CUSTOM(1);

    @Getter
    private final int type;
}
