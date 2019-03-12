package com.soeasy.wordfilter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhaoxiang
 * @create 2019/3/6
 **/
@Controller
public class LoginController {

    @RequestMapping("/login")
    public String login() {
        return "login";
    }
}
