package com.soeasy.wordfilter.service;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.tagging.SynonymTagging;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
        List<Word> words = WordSegmenter.segWithStopWords("违法");
        // 同义标注
        SynonymTagging.process(words);

        for(Word w: words){
            System.out.println(w.getText());
            w.getSynonym();
        }
        System.out.println(words);

    }


    public static void main(String[] args) {
        List<String> s = new ArrayList<>();
        s.add("123");
        s.add("222");
        s.add("333");


        System.out.println(s.toString());
    }
} 
