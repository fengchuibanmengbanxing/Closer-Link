package com_awake_CloserLink.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com_awake_CloserLink.Dto.Req.ShortLinkPageReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkGroupCountQueryRespDTO;
import com_awake_CloserLink.Entitys.LinkDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Author 清醒
 * @Date 2024/5/18 9:52
 */
@Mapper
public interface ShortLinkMapper extends BaseMapper<LinkDO> {
    List<ShortLinkGroupCountQueryRespDTO> getList(List<String> requestParam);
    @Update("update t_link set total_pv=total_pv+#{totalPv} , total_uv=total_uv+#{totalUv} , total_uip=total_uip+#{totalUip} where gid=#{gid} and full_short_url=#{fullShortUrl}")
    void incrementStats(@Param("gid") String gid,
                        @Param("fullShortUrl") String fullShortUrl,
                        @Param("totalUv") Integer totalUv,
                        @Param("totalPv") Integer totalPv,
                        @Param("totalUip") Integer totalUip
                        );


    IPage<LinkDO> pageLink( ShortLinkPageReqDTO shortLinkPageReqDTO);

}
