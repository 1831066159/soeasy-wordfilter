package com.soeasy.wordfilter.service.keywords;

import com.soeasy.wordfilter.model.KeyWord;
import com.soeasy.wordfilter.utils.AnalysisUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 关键词搜索器
 * -根据DFA生成搜索树(wordsTree)
 */
public class KWSeeker {

    /**
     * 所有的关键词
     */
    private Set<KeyWord> sensitiveWords;

    /**
     * 关键词树
     */
    public Map<String, Map> wordsTree = new ConcurrentHashMap<String, Map>();

    /**
     * 最短的关键词长度。用于对短于这个长度的文本不处理的判断，以节省一定的效率
     */
    public int wordLeastLen = 0;

    /**
     * @param sensitiveWords 关键词列表
     */
    public KWSeeker(Set<KeyWord> sensitiveWords) {
        this.sensitiveWords = sensitiveWords;
        reloadKWSeeker();
    }

    /**
     * 重新初始化-关键词树
     */
    public void reloadKWSeeker() {
        wordLeastLen = new DataInit().generalTree(sensitiveWords, wordsTree);
    }

    /**
     * 添加一个或多个新的关键词。
     *
     * @param newWord
     */
    public void addWord(List<KeyWord> newWord) {
        if (newWord != null && newWord.size() > 0) {
            for (KeyWord kw : newWord) {
                if (StringUtils.isNotEmpty(kw.getWord())) {
                    sensitiveWords.add(kw);
                }
            }
            reloadKWSeeker();
        }
    }

    /**
     * 删除一个或多个新的关键词。
     *
     * @param newWord
     */
    public void delWord(KeyWord... newWord) {
        if (newWord != null && newWord.length > 0) {
            for (KeyWord kw : newWord) {
                if (StringUtils.isNotEmpty(kw.getWord())) {
                    sensitiveWords.remove(kw);
                }
            }
            reloadKWSeeker();
        }
    }

    /**
     * 数据初始化
     */
    private static class DataInit {

        /**
         * 生成的临时词库树。用于在最后生成的时候一次性替换，尽量减少对正在查询时的干扰
         */
        private Map<String, Map> wordsTreeTmp = new HashMap<String, Map>();

        /**
         * 构造、生成词库树。并返回所有敏感词中最短的词的长度。
         *
         * @param sensitiveWords 词库
         * @param wordsTree      聚合词库的树
         * @return 返回所有敏感词中最短的词的长度。
         */
        public int generalTree(Set<KeyWord> sensitiveWords, Map<String, Map> wordsTree) {
            if (sensitiveWords == null || sensitiveWords.isEmpty() || wordsTree == null) {
                return 0;
            }

            wordsTreeTmp.clear();
            int len = 0;
            for (KeyWord kw : sensitiveWords) {
                if (len == 0) {
                    len = kw.getWordLength();
                } else if (kw.getWordLength() < len) {
                    len = kw.getWordLength();
                }
                AnalysisUtil.makeTreeByWord(wordsTreeTmp, StringUtils.trimToEmpty(kw.getWord()), kw);
            }
            wordsTree.clear();
            wordsTree.putAll(wordsTreeTmp);
            return len;
        }
    }


    @Override
    public String toString() {
        return "KWSeeker [sensitiveWords=" + sensitiveWords + ", wordsTree=" + wordsTree
                + ", wordLeastLen=" + wordLeastLen + "]";
    }


}
