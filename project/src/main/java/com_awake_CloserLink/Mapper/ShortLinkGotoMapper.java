package com_awake_CloserLink.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com_awake_CloserLink.Entitys.LinkGotoDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author 清醒
 * @Date 2024/5/20 12:02
 */

/**
 * 短链接跳转持久层
 */


@Mapper
public interface ShortLinkGotoMapper extends BaseMapper<LinkGotoDO> {


}
