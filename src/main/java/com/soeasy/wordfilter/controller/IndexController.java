package com.soeasy.wordfilter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhaoxiang
 * @create 2019/3/6
 **/
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index() {
        return "dashboard";
    }

    @RequestMapping("/newsManage")
    public String newsManage(Model model) {

        return "news/newsManage";
    }
}
