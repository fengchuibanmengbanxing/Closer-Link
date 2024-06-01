package com.awake.CloserLink.admin.Entitys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.awake.CloserLink.admin.Common.Database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 清醒
 * @Date 2024/5/16 20:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_group")
public class GroupDO extends BaseDO {
    /**
     * ID
     */
    private String id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;



}
