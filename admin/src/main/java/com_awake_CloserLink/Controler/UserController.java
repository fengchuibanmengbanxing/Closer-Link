package com_awake_CloserLink.Controler;

import cn.hutool.core.bean.BeanUtil;
import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Common.Convention.result.Results;
import com_awake_CloserLink.Dto.Request.UserRegister;
import com_awake_CloserLink.Dto.Respons.UserActualRespDTO;
import com_awake_CloserLink.Dto.Respons.UserRespDTO;
import com_awake_CloserLink.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author 清醒
 * @Date 2024/5/14 9:06
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/api/short-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {

        UserRespDTO usernameByUserName = userService.getUsernameByUserName(username);
        return Results.success(usernameByUserName);
    }


    @GetMapping("/api/short-link/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getUserActualByUsername(@PathVariable("username") String username) {
        UserRespDTO usernameByUserName = userService.getUsernameByUserName(username);
        return Results.success(BeanUtil.toBean(usernameByUserName, UserActualRespDTO.class));
    }

    @GetMapping("/api/short-link/v1/actual/has-username/{username}")
    public Result<Boolean> IsHasUsername(@PathVariable("username") String username) {
       Boolean isHasUsername = userService.isHasUsername(username);
       return Results.success(isHasUsername);
    }
    //注册用户
    @PostMapping("/api/short-link/v1/user")
    public Result<Void> IsHasUsername(@RequestBody UserRegister userRegister) {
        userService.register(userRegister);
        return Results.success();
    }



}
