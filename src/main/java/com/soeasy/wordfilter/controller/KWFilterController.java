package com.soeasy.wordfilter.controller;

import com.soeasy.wordfilter.service.IndexService;
import com.soeasy.wordfilter.service.ProcessorService;
import com.soeasy.wordfilter.utils.HttpClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 内容过滤ctrl
 *
 * @author
 * @create 2019/3/8
 **/
@Controller
public class KWFilterController {
    private static Logger logger = LoggerFactory.getLogger(KWFilterController.class);


    @Autowired
    private ProcessorService processorService;
    @Autowired
    private IndexService indexService;

    /**
     * 初始页跳转
     *
     * @return
     */
    @RequestMapping("/")
    public String defaultPage() {
        return "login";
    }


    /**
     * 首页
     *
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "index";
    }


    /**
     * 退出
     *
     * @return
     */
    @RequestMapping("logout")
    public String logout() {
        return "login";
    }

    /**
     * 登录
     *
     * @param inputName
     * @param inputPassword
     * @return
     */
    @RequestMapping(value = "login")
    public String dologin(@RequestParam(value = "inputName", required = true) String inputName,
                          @RequestParam(value = "inputPassword", required = true) String inputPassword) {

        try {
            Assert.hasLength(inputName, "用户名不能为空");
            Assert.hasLength(inputPassword, "密码不能为空");
            boolean checkRes = indexService.checkUser(inputName, inputPassword);
            if (checkRes) {
                return "index";
            }
        } catch (Exception e) {
            logger.error("登录异常", e);
        }
        return "login";
    }

    /**
     * 内容过滤页
     *
     * @param model
     * @return
     */
    @RequestMapping("welcome")
    public String filterContent(Model model) {

        model.addAttribute("code", "200");
        model.addAttribute("msg", "success");
        model.addAttribute("html1", "");
        model.addAttribute("html2", "");
        model.addAttribute("url", "");

        return "filter";
    }


    /**
     * 添加新关键词
     *
     * @param kws 过滤词1,过滤词2,过滤词3,……
     */
    @RequestMapping("addkw")
    @ResponseBody
    public String addkw(@RequestParam(value = "kws", required = true) String kws) {
        try {
            Assert.hasLength(kws, "参数异常:kws");
            processorService.addKws(Arrays.asList(kws.split(",")));

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
    @RequestMapping("filterkw")
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
                // 命中
                List<String> hitList = processorService.getHits(html);
                // 获取命中词的近义词
                Map<String, List<String>> synonyms = processorService.getSynonym(hitList);
                List<String> allsyn = new ArrayList<>();
                for (String k : synonyms.keySet()) {
                    allsyn.addAll(synonyms.get(k));
                }
                // 将近义词添加到敏感词树
                processorService.addKws(allsyn);
                // 过滤内容
                String filterRes = processorService.filterHtml(html);

                // 命中
                List<String> hitAll = processorService.getHits(html);

                model.addAttribute("url", url);
                model.addAttribute("type", type);
                // 原始内容
                model.addAttribute("html1", html);
                // 过滤后内容
                model.addAttribute("html2", filterRes);
                // 命中的词
                model.addAttribute("hits", "命中敏感词 : " + hitList.toString());
                logger.info("命中敏感词:{}", hitList);
                // 近义词
                model.addAttribute("synonym", "敏感词近义词 : " + synonyms.toString());
                logger.info("敏感词近义词:{}", synonyms);
                // 命中的全部
                model.addAttribute("hitall", "命中的全部词 : " + hitAll.toString());
                logger.info("命中的全部词:{}", hitAll);
                model.addAttribute("code", "200");
                model.addAttribute("msg", "success");
            }
        } catch (Exception e) {
            model.addAttribute("code", "400");
            model.addAttribute("msg", e.getMessage());
            logger.error("过滤指定url内容异常", e);
        }
        return "filter";
    }


    /**
     * 解析新浪新闻
     * 必须来源于: https://news.sina.com.cn/roll/#pageid=153&lid=2509&k=&num=50&page=1
     */
    public String getSinaContent(String url) {
        logger.info("解析sina-news: URL={}", url);
        try {
            String html = HttpClientUtil.doGet(url);
            Document doc = Jsoup.parse(html);
            Element content = null;

            content = doc.getElementById("artibody");
            if (content == null) {
                content = doc.getElementById("article");
            }
            if (content == null) {
                return "网站结构更新,内容解析出错,请更新解析算法";
            }
            return content.text();
        } catch (Exception e) {
            logger.error("解析新浪新闻异常", e);
        }
        return null;
    }

    /**
     * 解析网易新闻
     * 必须来源于: http://news.163.com/latest
     */
    public String get163Content(String url) {
        try {
            String html = HttpClientUtil.doGet(url);
            Document doc = Jsoup.parse(html);
            Element content = doc.getElementById("endText");
            if (content == null) {
                return "网站结构更新,内容解析出错,请更新解析算法";
            }
            return content.text();
        } catch (Exception e) {
            logger.error("解析网易新闻异常");
        }
        return null;
    }

}
