package com_awake_CloserLink.Remote.Req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * @Author 清醒
 * @Date 2024/5/18 20:07
 */

/**
 * 分页请求类
 */
@Data
public class ShortLinkPageReqDTO extends Page {
    /**
     * 分组标识
     */
    public String gid;
    /**
     * 排序标识
     */
    public String orderTag;
}
