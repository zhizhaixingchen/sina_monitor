package com.sina.dao.impl;
import com.sina.dao.SinaTopicDetailDao;
import com.sina.pojo.SinaTopicBean;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//话题详细信息定时器存储  与hive进行交互

@Repository
public class SinaTopicDetailDetailDaoImpl implements SinaTopicDetailDao {

    private JestClient jestClient;
    public SinaTopicDetailDetailDaoImpl(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    @Override
    public void storeSinaTopic(SinaTopicBean topicBean) {
/*        if(isFirst){
            initTopicIndex();
        }*/
        //注意：一个index只能存在一个type
        Index index = new Index.Builder(topicBean).index("sinatopic").type("topic").build();
        try {
            DocumentResult execute = jestClient.execute(index);
            System.out.println("结果 = "+execute.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取话题
    @Override
    public List<SinaTopicBean> getSinaTopicBeanFromDB(String keyword,int from) {
        List<SinaTopicBean> list = new ArrayList<>();
        String query_str = "{\n" +
                "\"query\": {\n" +
                "    \"term\": {\n" +
                "      \"keyword\": \""+keyword+"\"\n" +
                "    }\n" +
                "  },\n" +
                "\"from\":"+from+",\n" +
                "  \"size\": 10000\n" +
                ",\n" +
                "  \"sort\": [\n" +
                "    {\n" +
                "      \"current_date\": {\n" +
                "        \"order\": \"asc\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]"+
                "}";
        Search search = new Search.Builder(query_str).addIndex("sinatopic").addType("topic").build();
        try {
            SearchResult execute = jestClient.execute(search);
            int responseCode = execute.getResponseCode();
            System.out.println("状态码 = "+ responseCode);
            if(responseCode == 200){
                List<SearchResult.Hit<SinaTopicBean, Void>> hits = execute.getHits(SinaTopicBean.class);
                for (SearchResult.Hit<SinaTopicBean, Void> hit : hits) {
                    list.add(hit.source);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    //初始化index
    /*private void initTopicIndex(){
*//*        String setting = "{\n" +
                "        \"index\" : {\n" +
                "            \"analysis.analyzer.default.type\": \"ik_max_word\"\n" +
                "        }\n" +
                "    }";*//*
        String setting = "{\n" +
                "            analysis: {\n" +
                "                analyzer: {\n" +
                "                    default: {\n" +
                "                        type: keyword\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n";
        CreateIndex createIndex = new CreateIndex.Builder("sinatopic").settings(setting).build();
        try {
            JestResult execute = jestClient.execute(createIndex);
            System.out.println("execute = "+"初始化");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
