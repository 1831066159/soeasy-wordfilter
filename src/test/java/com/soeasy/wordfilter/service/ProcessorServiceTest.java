package com.soeasy.wordfilter.service;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.tagging.SynonymTagging;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProcessorService Tester.
 * https://blog.csdn.net/tototuzuoquan/article/details/70182695
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>三月 29, 2019</pre>
 */
public class ProcessorServiceTest {


    @Test
    public void testSP() {
        // 分词
        List<Word> words = WordSegmenter.segWithStopWords("安全");
        // 同义标注
        SynonymTagging.process(words);

        for(Word w: words){
            System.out.println(w.getText());
            w.getSynonym();
        }
        System.out.println(words);

    }


    public static void main(String[] args) {

        List<String> list = new ArrayList<>();
        list.add("安全");

        Map<String, List<String>> map =  getSynonym(list);

        System.out.println( map.toString());

    }


    /**
     * 获取命中词语义上相近的词
     *
     * @param hits
     */
    public static Map<String, List<String>> getSynonym(List<String> hits) {

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
}

