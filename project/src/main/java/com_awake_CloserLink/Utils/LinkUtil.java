package com_awake_CloserLink.Utils;

/**
 * @Author 清醒
 * @Date 2024/5/21 10:00
 */

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Optional;

/**
 * 短链接有效期
 */
public class LinkUtil {

    /**
     *
     * @param valid  用户规定有效期时间
     * @return  有效期
     */
    public static long getLinkCacheValidTime(Date valid) {

        return Optional.ofNullable(valid)
                .map(v -> DateUtil.between(new Date(),v, DateUnit.MS))
                .orElse(2419200000L);
    }
}
