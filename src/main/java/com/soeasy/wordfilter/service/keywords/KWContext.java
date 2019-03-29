package com.soeasy.wordfilter.service.keywords;

import com.soeasy.wordfilter.model.KeyWord;
import com.soeasy.wordfilter.model.keyWordResult;
import com.soeasy.wordfilter.utils.AnalysisUtil;
import com.soeasy.wordfilter.utils.EmojiUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 敏感词
 *
 * @author 没有蛀牙
 * @create 2019/3/7
 **/
public class KWContext {

    private static Logger logger = LoggerFactory.getLogger(KWContext.class);


    /**
     * 默认的单例，使用自带的敏感词库
     */
    public static volatile KWContext wordContext;

    /**
     * 自定义敏感词目录
     */
    private final static String SENSI_WORD_DIR = "/data/keystore/";
    /**
     * 自定义敏感词文件
     */
    private final static String SENSI_WORD_PATH = SENSI_WORD_DIR + "words.txt";
    /**
     * 默认敏感词文件
     */
    private final static String SENSI_WORD_LOCAL_PATH = "keystore/default_words.txt";
    /**
     * 敏感词库
     */
    public static KWSeeker seeker;

    /**
     * 单例
     *
     * @return
     */
    public static KWContext getInstance() {
        if (wordContext == null) {
            synchronized (KWContext.class) {
                if (wordContext == null) {
                    init();
                }
            }
        }
        return wordContext;
    }

    /**
     * 启动初始化字典
     * 如果是用户自定义文件,监听文件变化
     */
    static {
        boolean linuxFlag = init();
        if (linuxFlag) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        new FileWatch(SENSI_WORD_DIR).handleEvents(wordContext);
                    } catch (InterruptedException e) {
                        logger.error("文件监听失败", e);
                    }
                }
            }.start();
        }
    }


    /**
     * 构造函数
     *
     * @param reader
     */
    public KWContext(BufferedReader reader) {
        try {
            List<String> dataList = new ArrayList<String>();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                dataList.add(line);
            }
            initialize(dataList);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取字典文件
     *
     * @return
     */
    public static boolean init() {
        boolean linuxFlag = false;
        try {
            // 读取自定义文件
            wordContext = new KWContext(new BufferedReader(new InputStreamReader(new FileInputStream(SENSI_WORD_PATH), StandardCharsets.UTF_8)));
            linuxFlag = true;
        } catch (FileNotFoundException e) {
            // 读取系统默认文件
            wordContext = new KWContext(new BufferedReader(new InputStreamReader(KWContext.class.getClassLoader().getResourceAsStream(SENSI_WORD_LOCAL_PATH), StandardCharsets.UTF_8)));
        }
        return linuxFlag;
    }


    /**
     * 初始- 构建敏感库
     *
     * @param dataList 敏感词
     */
    private void initialize(List<String> dataList) {
        Set<KeyWord> kws;
        kws = new HashSet<KeyWord>();
        for (String word : dataList) {
            kws.add(new KeyWord(word));
        }
        seeker = new KWSeeker(kws);
    }


    /**
     * 过滤敏感词
     *
     * @param text
     * @return
     */
    public String wordFilter(String text) {
        logger.info("开始过滤敏感词……");
        text = EmojiUtil.filterEmoji(text);
        text = text.replace(" ", "");
        String formatStr = "***";
        return this.filter(seeker.wordsTree, text, formatStr, seeker.wordLeastLen);
    }

    /**
     * 将文本中的敏感词过滤掉
     *
     * @param wordsTree 关键词库树
     * @param text      待处理的文本
     * @param fomatStr  替换字符
     * @param minLen    敏感词最小长度
     * @return 返回过滤后的内容
     */
    private String filter(Map<String, Map> wordsTree, String text, String fomatStr, int minLen) {
        StringBuffer result = new StringBuffer("");
        String pre = null;
        // 词的前面一个字
        while (true) {
            if (wordsTree == null || wordsTree.isEmpty() || StringUtils.isEmpty(text)) {
                return result.append(text).toString();
            }
            // 过滤文本小于敏感词最小长度,未命中直接返回
            if (text.length() < minLen) {
                return result.append(text).toString();
            }
            String chr = text.substring(0, 1);
            text = text.substring(1);
            Map<String, Map> nextWord = wordsTree.get(chr);
            // 没有对应的下一个字，表示这不是关键词的开头，进行下一个循环
            if (nextWord == null) {
                result.append(chr);
                pre = chr;
                continue;
            }

            KeyWord kw = AnalysisUtil.getSensitiveWord(chr, pre, nextWord, text);
            // 没有匹配到完整关键字，下一个循环
            if (kw == null) {
                result.append(chr);
                pre = chr;
                continue;
            }

            // 处理命中片段,替换*
            result.append(fomatStr);
            // 从text中去除当前已经匹配的内容，进行下一个循环匹配
            text = text.substring(kw.getWordLength() - 1);
            pre = kw.getWord().substring(kw.getWordLength() - 1, kw.getWordLength());
            continue;
        }
    }


    /**
     * 获取命中的敏感词
     *
     * @param text
     * @return
     */
    public List<keyWordResult> getHits(String text) {
        logger.info("开始获取命中的敏感词……");
        text = EmojiUtil.filterEmoji(text);
        text = text.replace(" ", "");
        return this.getHits(seeker.wordsTree, text, seeker.wordLeastLen);
    }

    /**
     * 获取命中的敏感词
     *
     * @param wordsTree 关键词库树
     * @param text      待处理的文本
     * @param minLen    敏感词最小长度
     * @return 命中的敏感词
     */
    private List<keyWordResult> getHits(Map<String, Map> wordsTree, String text, int minLen) {
        // 词的前面一个字
        String pre = null;
        // 词匹配的开始位置
        int startPosition = 0;
        // 返回命中的关键词结果
        List<keyWordResult> rs = new ArrayList<keyWordResult>();

        while (true) {
            try {
                if (wordsTree == null || wordsTree.isEmpty() || StringUtils.isEmpty(text)) {
                    return rs;
                }
                if (text.length() < minLen) {
                    return rs;
                }
                String chr = text.substring(0, 1);
                text = text.substring(1);
                Map<String, Map> nextWord = wordsTree.get(chr);
                // 没有对应的下一个字，表示这不是关键词的开头，进行下一个循环
                if (nextWord == null) {
                    pre = chr;
                    continue;
                }
                KeyWord kw = AnalysisUtil.getSensitiveWord(chr, pre, nextWord, text);
                if (kw == null) {
                    // 没有匹配到完整关键字，下一个循环
                    pre = chr;
                    continue;
                }
                // 同一个word多次出现记录在一起
                keyWordResult result = new keyWordResult(startPosition, kw.getWord());
                int index = rs.indexOf(result);
                if (index > -1) {
                    rs.get(index).addPosition(startPosition, kw.getWord());
                } else {
                    rs.add(result);
                }
                // 从text中去除当前已经匹配的内容，进行下一个循环匹配
                text = text.substring(kw.getWordLength() - 1);
                pre = kw.getWord().substring(kw.getWordLength() - 1, kw.getWordLength());
                continue;
            } finally {
                if (pre != null) {
                    startPosition = startPosition + pre.length();
                }
            }
        }

    }


    public void addKW(String kw) {
        logger.info("添加新关键词:{}", kw);
        List<KeyWord> keyWords = new ArrayList<>();
        keyWords.add(new KeyWord(kw));
        seeker.addWord(keyWords);
    }

    /**
     * 添加新关键词
     *
     * @param newWord
     */
    public void addKW(List<String> keylist) {
        logger.info("添加新关键词", keylist.size());
        List<KeyWord> keyWords = new ArrayList<>();
        for (String key : keylist) {
            keyWords.add(new KeyWord(key));
        }
        seeker.addWord(keyWords);
    }

}
