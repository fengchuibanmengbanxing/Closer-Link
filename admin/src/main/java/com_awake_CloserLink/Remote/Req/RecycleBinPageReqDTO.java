package com_awake_CloserLink.Remote.Req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author 清醒
 * @Date 2024/5/21 22:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecycleBinPageReqDTO extends Page {

    /**
     * 分组标识集合
     */
    private List<String> gidList;

}
