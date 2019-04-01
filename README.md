# soeasy-wordfilter

内容必须来源于以下站点:
https://news.sina.com.cn/roll/#pageid=153&lid=2509&k=&num=50&page=1
http://news.163.com/latest


运行环境要求:
jdk1.8
maven3



提供了多种基于词典的分词算法，
并利用ngram模型来消除歧义。
能准确识别英文、数字，以及日期、时间等数量词，能识别人名、地名、组织机构名等未登录词。
能通过自定义配置文件来改变组件行为，
能自定义用户词库、自动检测词库变化、支持大规模分布式环境，能灵活指定多种分词算法，
能使用refine功能灵活控制分词结果，
还能使用词频统计、词性标注、同义标注、反义标注、拼音标注等功能。

提供了10种分词算法:
正向最大匹配算法：MaximumMatching
逆向最大匹配算法：ReverseMaximumMatching
正向最小匹配算法：MinimumMatching
逆向最小匹配算法：ReverseMinimumMatching
双向最大匹配算法：BidirectionalMaximumMatching
双向最小匹配算法：BidirectionalMinimumMatching
双向最大最小匹配算法：BidirectionalMaximumMinimumMatching
全切分算法：FullSegmentation
最少词数算法：MinimalWordCount
最大Ngram分值算法：MaxNgram