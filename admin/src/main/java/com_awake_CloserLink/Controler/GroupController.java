package com_awake_CloserLink.Controler;

import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Common.Convention.result.Results;
import com_awake_CloserLink.Dto.Request.ShortLinkSaveGroupReqDTO;
import com_awake_CloserLink.Dto.Respons.ShortLinkGroupRespDTO;
import com_awake_CloserLink.Service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author 清醒
 * @Date 2024/5/16 20:31
 */
@RestController
public class GroupController {
    @Autowired
    private GroupService groupService;

    /**
     * 保存短链接组
     * @param
     * @return
     */
    @PostMapping("/api/short-link/v1/group")
    public Result<Void> saveGroup(@RequestBody ShortLinkSaveGroupReqDTO shortLinkSaveGroupReqDTO){
        groupService.saveGroup(shortLinkSaveGroupReqDTO.getGroupName());
        return Results.success();
    }
    /**
     * 返回短链接分组集合
     * @param
     * @return
     */
    @GetMapping("/api/short-link/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listGroup(){
        List<ShortLinkGroupRespDTO> list=groupService.listGroup();
        return Results.success(list);
    }
}
