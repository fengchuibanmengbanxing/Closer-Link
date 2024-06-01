package com.awake.CloserLink.admin.Controler;

import cn.hutool.core.bean.BeanUtil;
import com.awake.CloserLink.admin.Dto.Request.UserLoginReqDTO;
import com.awake.CloserLink.admin.Dto.Request.UserRegister;
import com.awake.CloserLink.admin.Dto.Respons.UserActualRespDTO;
import com.awake.CloserLink.admin.Dto.Respons.UserLoginRespDTO;
import com.awake.CloserLink.admin.Dto.Respons.UserRespDTO;
import com.awake.CloserLink.admin.Service.UserService;
import com.awake.CloserLink.admin.Common.Convention.result.Result;
import com.awake.CloserLink.admin.Common.Convention.result.Results;
import com.awake.CloserLink.admin.Dto.Request.UserUpdateReqDTO;
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

    //查询脱敏用户信息
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {

        UserRespDTO usernameByUserName = userService.getUsernameByUserName(username);
        return Results.success(usernameByUserName);
    }

    //查询用户信息
    @GetMapping("/api/short-link/admin/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getUserActualByUsername(@PathVariable("username") String username) {
        UserRespDTO usernameByUserName = userService.getUsernameByUserName(username);
        return Results.success(BeanUtil.toBean(usernameByUserName, UserActualRespDTO.class));
    }
   //查看用户名是否存在
    @GetMapping("/api/short-link/admin/v1/actual/has-username/{username}")
    public Result<Boolean> IsHasUsername(@PathVariable("username") String username) {
       Boolean isHasUsername = userService.isHasUsername(username);
       return Results.success(isHasUsername);
    }


    //注册新用户
    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> UserRegister(@RequestBody UserRegister userRegister) {
        userService.register(userRegister);
        return Results.success();
    }

    //修改用户信息
    @PutMapping("/api/short-link/admin/v1/user")
    public Result<Void> UpdateUser(@RequestBody UserUpdateReqDTO userUpdateReqDTO) {
        int i = userService.update_user(userUpdateReqDTO);
        if (i == 0) {
            return Results.failure();
        }
        return Results.success();
    }
    //用户登录
    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO userLoginReqDTO) {
        UserLoginRespDTO userLoginRespDTO = userService.login(userLoginReqDTO);
        return Results.success(userLoginRespDTO);
    }
    //校验用户登录状态
    @GetMapping("/api/short-link/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("token")String token,@RequestParam("username")String username) {
        Boolean check=userService.checkLogin(token,username);
        return Results.success(check);
    }

    //用户登录退出
    @DeleteMapping ("/api/short-link/admin/v1/user/logout")
    public Result<Void> logout(@RequestParam("token")String token,@RequestParam("username")String username) {
       userService.logout(token,username);
        return Results.success();
    }
}
