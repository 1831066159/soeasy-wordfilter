package com.soeasy.wordfilter.service.keywords;

import com.soeasy.wordfilter.model.keyWordResult;
import org.junit.Test;

import java.util.List;

/**
 * KeyWordContext Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>三月 8, 2019</pre>
 */
public class KeyWordContextTest {


    /**
     * 测试关键词树加载
     */
    @Test
    public void testInit() {
        KWContext ctx = KWContext.getInstance();
        KWSeeker seeker = ctx.seeker;
        for (String key : seeker.wordsTree.keySet()) {

            System.out.println(key + "---" + seeker.wordsTree.get(key));
        }
    }

    /**
     * 测试内容过滤
     *
     * @throws Exception
     */
    @Test
    public void testWordFilter() throws Exception {
        String sentence = "我是一个T M D(他妈的) 法轮功AB违禁词，追杀本拉登";
        KWContext ctx = KWContext.getInstance();
        String res = ctx.wordFilter(sentence);
        System.out.println("过滤后: " + res);

    }


    /**
     * 测试命中的关键词
     */
    @Test
    public void testGetHits() throws Exception {

        String sentence = "我是一个T M D(他妈的) 法轮功AB违禁词，追杀本拉登";
        KWContext ctx = KWContext.getInstance();
        List<keyWordResult> list = ctx.getHits(sentence);
        for (keyWordResult res : list) {
            System.out.println("命中: " + res.getWord());
        }
    }

    /**
     * 测试添加新关键词
     */
    @Test
    public void testAddKW() {
        String sentence = "我是一个T M D(他妈的) 法轮功AB违禁词，追杀本拉登";
        KWContext ctx = KWContext.getInstance();
        // 向过滤器增加一个词，额外造个词
        ctx.addKW("AB违禁词");
        String res = ctx.wordFilter(sentence);
        System.out.println("过滤后: " + res);
    }

} 
