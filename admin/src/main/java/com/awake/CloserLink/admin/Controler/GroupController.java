package com.awake.CloserLink.admin.Controler;

import com.awake.CloserLink.admin.Dto.Request.ShortLinkSaveGroupReqDTO;
import com.awake.CloserLink.admin.Dto.Request.ShortLinkSortGroupReqDTO;
import com.awake.CloserLink.admin.Dto.Respons.ShortLinkGroupRespDTO;
import com.awake.CloserLink.admin.Service.GroupService;
import com.awake.CloserLink.admin.Common.Convention.result.Result;
import com.awake.CloserLink.admin.Common.Convention.result.Results;
import com.awake.CloserLink.admin.Dto.Request.ShortLinkUpdateGroupReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> saveGroup(@RequestBody ShortLinkSaveGroupReqDTO shortLinkSaveGroupReqDTO){
        groupService.saveGroup(shortLinkSaveGroupReqDTO.getGroupName());
        return Results.success();
    }
    /**
     * 返回短链接分组集合
     * @param
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listGroup(){
        List<ShortLinkGroupRespDTO> list=groupService.listGroup();
        return Results.success(list);
    }

    /**
     * 修改短链接分组信息
     * @return
     */
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestBody ShortLinkUpdateGroupReqDTO shortLinkUpdateGroupReqDTO){
        groupService.updateGroup(shortLinkUpdateGroupReqDTO);
        return Results.success();
    }

    /**
     * 删除短链接分组名
     * @param
     * @return
     */
    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> deleteGroup(@RequestParam("gid")String gid){
        groupService.deleteGroup(gid);
        return Results.success();
    }

    /**
     * 对短链接分组进行排序
     * @param
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/group/sort")
    public Result<Void>sortGroup(@RequestBody List<ShortLinkSortGroupReqDTO> shortLinkSortGroupReqDTOList){
        //对集合中的数据进行排序根据sortOrder
        groupService.sortGroup(shortLinkSortGroupReqDTOList);
        return Results.success();
    }

}
