package com.awake.CloserLink.project.Dto.Req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.awake.CloserLink.project.Entitys.LinkDO;
import lombok.Data;

/**
 * @Author 清醒
 * @Date 2024/5/18 20:07
 */

/**
 * 分页请求类
 */
@Data
public class ShortLinkPageReqDTO extends Page<LinkDO> {
    /**
     * 分组标识
     */
    public String gid;
    /**
     * 排序标识
     */
    public String orderTag;
}
