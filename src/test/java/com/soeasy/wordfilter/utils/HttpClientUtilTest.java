package com.soeasy.wordfilter.utils;

import com.soeasy.wordfilter.service.keywords.KWContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

/**
 * HttpClientUtil Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>三月 8, 2019</pre>
 */
public class HttpClientUtilTest {


    @Test
    public void testDoGet() throws Exception {
        String url = "http://tech.sina.com.cn/csj/2019-03-08/doc-ihrfqzkc2225913.shtml";
        String ctx2 = HttpClientUtil.doGet(url);
        System.out.println(ctx2);
    }


    /**
     * 新浪新闻 https://news.sina.com.cn/roll/#pageid=153&lid=2509&k=&num=50&page=1
     */
    @Test
    public void testParseSina() {
        String url = "http://tech.sina.com.cn/csj/2019-03-08/doc-ihrfqzkc2225913.shtml";
        String html = HttpClientUtil.doGet(url);

        Document doc = Jsoup.parse(html);
        Element content = doc.getElementById("artibody");
        //System.out.println(content.toString());
        KWContext ctx = KWContext.getInstance();
        ctx.addKW("王朴石");
        String res = ctx.wordFilter(content.toString());

        System.out.println(res);
    }


    /**
     * 网易新闻 http://news.163.com/latest
     */
    @Test
    public void testParse163() {
        String url = "https://news.163.com/19/0308/14/E9OJMMBG0001899N.html";
        String html = HttpClientUtil.doGet(url);
        Document doc = Jsoup.parse(html);
        Element content = doc.getElementById("endText");
        KWContext ctx = KWContext.getInstance();
        ctx.addKW("王毅");
        String res = ctx.wordFilter(content.toString());

        System.out.println(res);
    }
}
