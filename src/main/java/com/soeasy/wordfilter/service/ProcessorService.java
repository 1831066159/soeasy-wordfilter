package com.soeasy.wordfilter.service;

import com.soeasy.wordfilter.mapper.UserMapper;
import com.soeasy.wordfilter.model.keyWordResult;
import com.soeasy.wordfilter.service.keywords.KWContext;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.tagging.SynonymTagging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 * @create 2019/3/7
 **/
@Service
public class ProcessorService {

    private static Logger logger = LoggerFactory.getLogger(ProcessorService.class);




    /**
     * 获取命中的敏感词
     *
     * @param html
     */
    public List<String> getHits(String html) {
        logger.info("查询命中的敏感词");
        List<String> hits = new ArrayList<>();
        // 获取命中的词
        List<keyWordResult> kws = KWContext.getInstance().getHits(html);
        for (keyWordResult kw : kws) {
            hits.add(kw.getWord());
        }
        return hits;
    }


    /**
     * 内容过滤
     *
     * @param html
     */
    public String filterHtml(String html) {
        return KWContext.getInstance().wordFilter(html);
    }


    /**
     * 获取命中词语义上相近的词
     *
     * @param hits
     */
    public Map<String, List<String>> getSynonym(List<String> hits) {

        Map<String, List<String>> resmap = new HashMap<>();

        for (String str : hits) {
            List<String> synonyms = new ArrayList<>();
            // 进行分词
            List<Word> words = WordSegmenter.segWithStopWords(str);
            // 做同义标注
            SynonymTagging.process(words);
            for (Word w : words) {
                int i=1;
                // 获取同义词
                for (Word wy : w.getSynonym()) {
                    if(i++>20){
                      break;
                    }
                    synonyms.add(wy.getText());
                }
            }
            resmap.put(str, synonyms);
        }
        return resmap;
    }


    /**
     * 向过滤器增加自定义词
     *
     * @param kws
     */
    public void addKws(List<String> kws) {
        // 向过滤器增加自定义词
        KWContext.getInstance().addKWstrList(kws);
    }


}
