package com.sina.dao.impl;

import com.sina.dao.SinaTopicMonitorDao;
import com.sina.pojo.ScanTopicBean;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SinaTopicMonitorDaoImpl implements SinaTopicMonitorDao {


    private JestClient jestClient;
    public SinaTopicMonitorDaoImpl(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    @Override
    public List<ScanTopicBean> queryByTime(long beginTime, long endTime, int page) {
        List<ScanTopicBean> list = new ArrayList<>();
        String query ="{\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"should\": [\n" +
                "        {\"term\": {\n" +
                "            \"monitor\": true\n" +
                "        }},{\n" +
                "          \"range\": {\n" +
                "            \"current_date\": {\n" +
                "              \"gte\": "+beginTime+",\n" +
                "                \"lte\": "+endTime+"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "      \"from\": "+page+",\n" +
                "      \"size\": 10000\n" +
                "}";
        Search search = new Search.Builder(query).addIndex("sinatopicscan").addType("scan").build();
        try {
            SearchResult execute = jestClient.execute(search);
            if(execute.getResponseCode() == 200){
                List<SearchResult.Hit<ScanTopicBean, Void>> hits = execute.getHits(ScanTopicBean.class);
                for (SearchResult.Hit<ScanTopicBean, Void> hit : hits) {
                    ScanTopicBean source = hit.source;
                    list.add(source);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ScanTopicBean> queryByContent(String content) {
        List<ScanTopicBean> list = new ArrayList<>();
        String query="{\n" +
                "  \"query\": {\n" +
                "    \"match_phrase\": {\n" +
                "      \"topicName\": \""+content+"\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"highlight\": {\n" +
                "    \"pre_tags\": [\"<em style='color:red;font-style:normal'>\"],\n" +
                "    \"post_tags\": [\"</em>\"], \n" +
                "      \"fields\": {\n" +
                "        \"topicName\": {}\n" +
                "      }\n" +
                "  }\n" +
                "}\n";
        Search build = new Search.Builder(query).addIndex("sinatopicscan").addType("scan").build();
        try {
            SearchResult execute = jestClient.execute(build);
            if(execute.getResponseCode() == 200){
                List<SearchResult.Hit<ScanTopicBean, Void>> hits = execute.getHits(ScanTopicBean.class);
                for (SearchResult.Hit<ScanTopicBean, Void> hit : hits) {
                    ScanTopicBean scanTopicBean = hit.source;
                    List<String> topicNameHighlight = hit.highlight.get("topicName");
                    if(topicNameHighlight!=null){
                        scanTopicBean.setTopicName(topicNameHighlight.get(0));
                        list.add(scanTopicBean);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
