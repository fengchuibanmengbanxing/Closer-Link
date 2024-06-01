package com.awake.CloserLink.project.Controller;

import com.awake.CloserLink.project.Common.Convention.result.Result;
import com.awake.CloserLink.project.Common.Convention.result.Results;
import com.awake.CloserLink.project.Service.UrlTitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 清醒
 * @Date 2024/5/21 12:35
 */
@RestController
public class UrlTitleController {

    @Autowired
    private UrlTitleService urlTitleService;
    @GetMapping("/api/short-link/title")
    public Result<String> getUrlTitle(@RequestParam("url") String url){
        return Results.success(urlTitleService.getUrlTitle(url));
    }

}
