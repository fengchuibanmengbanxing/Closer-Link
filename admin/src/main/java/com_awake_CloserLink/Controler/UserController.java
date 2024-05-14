package com_awake_CloserLink.Controler;

import com_awake_CloserLink.Common.Convention.result.Result;
import com_awake_CloserLink.Common.Convention.result.Results;
import com_awake_CloserLink.Dto.Respons.UserRespDTO;
import com_awake_CloserLink.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 清醒
 * @Date 2024/5/14 9:06
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {

        UserRespDTO usernameByUserName = userService.getUsernameByUserName(username);
        if (usernameByUserName != null) {
            return Results.success(usernameByUserName);
        } else {
            return null;
        }

    }

}
