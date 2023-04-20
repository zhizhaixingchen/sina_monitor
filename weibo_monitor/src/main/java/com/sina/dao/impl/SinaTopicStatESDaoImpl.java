package com.sina.dao.impl;

import com.sina.dao.SinaTopicStatESDao;
import com.sina.pojo.SinaTopicESStatBean;
import com.sina.util.AnsjWord;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.*;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SinaTopicStatESDaoImpl implements SinaTopicStatESDao {
    private JestClient jestClient;
    public SinaTopicStatESDaoImpl(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    @Override
    public List<SinaTopicESStatBean> queryAll(String key,String type) {
        List<SinaTopicESStatBean> list = new ArrayList<>();
        String query = "{\n" +
                "  \"size\":0,\n" +
                "  \"aggs\": {\n" +
                "    \"group_by_keyword\": {\n" +
                "      \"terms\": {\n" +
                "        \"field\": \"keyword.keyword\",\n" +
                "        \"size\": 5000,\n" +
                "        \"order\": {\n" +
                "          \""+key+"\": \""+type+"\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"aggs\": {\n" +
                "        \"beginTime\": {\n" +
                "          \"min\": {\n" +
                "            \"field\": \"current_date\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"endTime\":{\n" +
                "          \"max\": {\n" +
                "            \"field\": \"current_date\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"maxHot\":{\n" +
                "          \"max\": {\n" +
                "            \"field\": \"access_count\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"emotion\":{\n" +
                "          \"avg\": {\n" +
                "            \"field\": \"emotion_point\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"highStage\":{\n" +
                "          \"min\":{\n" +
                "            \"field\": \"stage\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"lowStage\":{\n" +
                "          \"max\":{\n" +
                "            \"field\": \"stage\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Search build = new Search.Builder(query).addIndex("sina").addType("hot").build();
        try {
            SearchResult execute = jestClient.execute(build);
            if(execute.getResponseCode() == 200){
                MetricAggregation aggregations = execute.getAggregations();
                List<TermsAggregation.Entry> buckets = aggregations.getTermsAggregation("group_by_keyword").getBuckets();
                for (TermsAggregation.Entry bucket : buckets) {
                    SinaTopicESStatBean stes = getSinaTopicESStatBean(bucket);
                    list.add(stes);
                }
                System.out.println("debug");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public SinaTopicESStatBean queryByKeyword(String keyword) {
        SinaTopicESStatBean stat = null;
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"match_phrase\": {\n" +
                "      \"keyword\": \""+keyword+"\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"aggs\": {\n" +
                "    \"beginTime\": {\n" +
                "      \"min\": {\n" +
                "        \"field\": \"current_date\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"endTime\":{\n" +
                "      \"max\": {\n" +
                "        \"field\": \"current_date\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"maxHot\":{\n" +
                "      \"max\": {\n" +
                "        \"field\": \"access_count\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"emotion\":{\n" +
                "      \"avg\": {\n" +
                "        \"field\": \"emotion_point\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"highStage\":{\n" +
                "      \"min\":{\n" +
                "        \"field\": \"stage\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"lowStage\":{\n" +
                "      \"max\":{\n" +
                "        \"field\": \"stage\"\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"size\": 1\n" +
                "}";
        Search build = new Search.Builder(query).addIndex("sina").addType("hot").build();
        try {
            SearchResult execute = jestClient.execute(build);
            if(execute.getResponseCode() == 200){
                //获取聚合参数
                MetricAggregation aggregations = execute.getAggregations();
                stat = getSinaTopicESStatBean(aggregations);
                stat.setKeyword(keyword);
                stat.setAllWords(AnsjWord.nlpWord(keyword));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stat;
    }

    private SinaTopicESStatBean getSinaTopicESStatBean(TermsAggregation.Entry bucket) {
        //话题名称
        String keyword = bucket.getKey();
        //开始时间
        MinAggregation min_starttime_agg = bucket.getMinAggregation("beginTime");
        double starttime_d = min_starttime_agg.getMin();
        long startTime = (long)starttime_d;
        //结束时间
        MaxAggregation max_endtime_agg = bucket.getMaxAggregation("endTime");
        double endtime_d = max_endtime_agg.getMax();
        long endTime = (long)endtime_d;
        //最大热度
        MaxAggregation max_hot_agg = bucket.getMaxAggregation("maxHot");
        double access_count_d = max_hot_agg.getMax();
        long access_count = (long)access_count_d;
        //话题情感
        AvgAggregation avg_emotion_agg = bucket.getAvgAggregation("emotion");
        double emotion = avg_emotion_agg.getAvg();
        //最高排名
        MaxAggregation max_stage_agg = bucket.getMaxAggregation("highStage");
        double max_stage_d = max_stage_agg.getMax();
        int high_stage = (int)max_stage_d;
        //最低排名
        MinAggregation min_stage_agg = bucket.getMinAggregation("lowStage");
        double min_stage_d = min_stage_agg.getMin();
        int low_stage = (int)min_stage_d;
        //判断是否热门方法 检测当前话题最终时间距离现在是否超过60s    90*1000
        long now = System.currentTimeMillis();
        boolean ishot = true;
        if(now-endTime>60000){
            ishot = false;
        }
        SinaTopicESStatBean sinaTopicESStatBean = new SinaTopicESStatBean(keyword, access_count, startTime, endTime, ishot, emotion, low_stage, high_stage, AnsjWord.nlpWord(keyword));
        return sinaTopicESStatBean;
    }

    private SinaTopicESStatBean getSinaTopicESStatBean(MetricAggregation bucket) {

        //话题名称
        String keyword = "";
        //开始时间
        MinAggregation min_starttime_agg = bucket.getMinAggregation("beginTime");
        double starttime_d = min_starttime_agg.getMin();
        long startTime = (long)starttime_d;
        //结束时间
        MaxAggregation max_endtime_agg = bucket.getMaxAggregation("endTime");
        double endtime_d = max_endtime_agg.getMax();
        long endTime = (long)endtime_d;
        //最大热度
        MaxAggregation max_hot_agg = bucket.getMaxAggregation("maxHot");
        double access_count_d = max_hot_agg.getMax();
        long access_count = (long)access_count_d;
        //话题情感
        AvgAggregation avg_emotion_agg = bucket.getAvgAggregation("emotion");
        double emotion = avg_emotion_agg.getAvg();
        //最高排名
        MaxAggregation max_stage_agg = bucket.getMaxAggregation("highStage");
        double max_stage_d = max_stage_agg.getMax();
        int high_stage = (int)max_stage_d;
        //最低排名
        MinAggregation min_stage_agg = bucket.getMinAggregation("lowStage");
        double min_stage_d = min_stage_agg.getMin();
        int low_stage = (int)min_stage_d;
        //判断是否热门方法 检测当前话题最终时间距离现在是否超过60s    90*1000
        long now = System.currentTimeMillis();
        boolean ishot = true;
        if(now-endTime>60000){
            ishot = false;
        }
        SinaTopicESStatBean sinaTopicESStatBean = new SinaTopicESStatBean(keyword, access_count, startTime, endTime, ishot, emotion, low_stage, high_stage, AnsjWord.nlpWord(keyword));
        return sinaTopicESStatBean;
    }

}
