package com.soeasy.wordfilter.controller;

import com.soeasy.wordfilter.model.keyWordResult;
import com.soeasy.wordfilter.service.keywords.KWContext;
import com.soeasy.wordfilter.utils.HttpClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 没有蛀牙
 * @create 2019/3/8
 **/
@Controller
public class KWFilterController {
    private static Logger logger = LoggerFactory.getLogger(KWFilterController.class);


    @RequestMapping("/")
    public String index() {
        return "dashboard";
    }

    /**
     * 添加新关键词
     *
     * @param kws 过滤词1,过滤词2,过滤词3,……
     */
    @RequestMapping("addkw")
    public String addkw(@RequestParam(value = "kws", required = true) String kws) {
        try {
            Assert.hasLength(kws, "参数异常:kws");
            KWContext ctx = KWContext.getInstance();
            for (String key : kws.split(",")) {
                // 向过滤器增加一个词，额外造个词
                ctx.addKW(key);
            }
        } catch (Exception e) {
            logger.error("添加新关键词失败", e);
            return "400";
        }
        return "200";
    }


    /**
     * 过滤指定url内容
     *
     * @param url
     * @param type
     * @param model
     * @return
     */
    @RequestMapping("news")
    public String kwFilter(@RequestParam(value = "url", required = true) String url, @RequestParam(value = "type", required = true) String type, Model model) {
        try {
            // 参数检查
            Assert.hasLength(url, "参数异常:url");
            Assert.hasLength(type, "参数异常:type");

            String html = null;
            if ("sina".equals(type)) {
                html = this.getSinaContent(url);
            } else if ("163".equals(type)) {
                html = this.get163Content(url);
            }

            if (!StringUtils.isEmpty(html)) {
                KWContext ctx = KWContext.getInstance();
                String filterRes = ctx.wordFilter(html);
                // 原始内容
                model.addAttribute("html1", html);
                // 过滤后内容
                model.addAttribute("html2", filterRes);
                // 获取命中的词
                List<keyWordResult> list = ctx.getHits(html);
                model.addAttribute("hit", list);

                model.addAttribute("code", "200");
                model.addAttribute("msg", "success");
            }
        } catch (Exception e) {
            model.addAttribute("code", "400");
            model.addAttribute("msg", e.getMessage());
        }
        return "filter";
    }


    /**
     * 新浪新闻
     * URL来源: https://news.sina.com.cn/roll/#pageid=153&lid=2509&k=&num=50&page=1
     */
    public String getSinaContent(String url) {
        logger.info("解析sina-news: URL={}", url);
        try {
            String html = HttpClientUtil.doGet(url);
            Document doc = Jsoup.parse(html);
            Element content = doc.getElementById("artibody");
            return content.text();
        } catch (Exception e) {
            logger.error("解析新浪新闻异常");
        }
        return null;
    }

    /**
     * 网易新闻
     * URL来源: http://news.163.com/latest
     */
    public String get163Content(String url) {
        try {
            String html = HttpClientUtil.doGet(url);
            Document doc = Jsoup.parse(html);
            Element content = doc.getElementById("endText");
            return content.text();
        } catch (Exception e) {
            logger.error("解析网易新闻异常");
        }
        return null;
    }

}
