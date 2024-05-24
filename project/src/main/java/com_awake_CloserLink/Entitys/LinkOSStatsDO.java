package com_awake_CloserLink.Entitys;

import com.baomidou.mybatisplus.annotation.TableName;
import com_awake_CloserLink.Common.Database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author 清醒
 * @Date 2024/5/24 15:32
 */
@Data
@TableName("t_link_os_stats")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkOSStatsDO extends BaseDO {
    /**
     * ID
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 日期
     */
    private Date date;

    /**
     * 访问量
     */
    private Integer cnt;

    /**
     * 操作系统
     */
    private String os;
}
