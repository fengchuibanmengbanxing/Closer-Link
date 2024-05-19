package com_awake_CloserLink.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com_awake_CloserLink.Dto.Resp.ShortLinkGroupCountQueryRespDTO;
import com_awake_CloserLink.Entitys.LinkDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author 清醒
 * @Date 2024/5/18 9:52
 */
@Mapper
public interface ShortLinkMapper extends BaseMapper<LinkDO> {
    List<ShortLinkGroupCountQueryRespDTO> getList(List<String> requestParam);
}
