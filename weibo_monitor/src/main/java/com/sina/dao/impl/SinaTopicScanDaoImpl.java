package com.sina.dao.impl;

import com.google.gson.JsonSyntaxException;
import com.sina.dao.SinaTopicScanDao;
import com.sina.pojo.ScanTopicBean;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import io.searchbox.indices.CreateIndex;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//话题搜索  一个话题只对应一个document(行)

@Repository
public class SinaTopicScanDaoImpl implements SinaTopicScanDao {

    private final JestClient jestClient;
    public SinaTopicScanDaoImpl(JestClient jestClient) {
        this.jestClient = jestClient;
    }


    //我们利用ES主键相同覆盖的原理，我们只进行插入
    //排序：由current_date控制
    @Override
    public void insertTopic(ScanTopicBean scanTopicBean) {
        //建表    判断index是否存在
/*        if(!indexIsExist()){
            initTopicScanIndex();
        }*/
        Index index = new Index.Builder(scanTopicBean).index("sinatopicscan").type("scan").build();
        try {
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ScanTopicBean> queryFive() {
        List<ScanTopicBean> list = new ArrayList<>();
        String query_str = "{\n" +
                "  \"query\": {\n" +
                "    \"match_all\": {}\n" +
                "  },\n" +
                "  \"sort\": [\n" +
                "    {\n" +
                "      \"current_date\": {\n" +
                "        \"order\": \"asc\"\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"from\": 0\n" +
                "  , \"size\": 5\n" +
                "}";
        Search search = new Search.Builder(query_str).addIndex("sinatopicscan").addType("scan").build();
        try {
            SearchResult execute = jestClient.execute(search);
            if(execute.getResponseCode() == 200){
                List<SearchResult.Hit<ScanTopicBean, Void>> hits = execute.getHits(ScanTopicBean.class);
                for (SearchResult.Hit<ScanTopicBean, Void> hit : hits) {
                    list.add(hit.source);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<String> queryPhrase(String keyword) {
        List<String> list = new ArrayList<>();
        String query="{\n" +
                "  \"query\": {\n" +
                "    \"match_phrase\": {\n" +
                "      \"topicName\": \""+keyword+"\"\n" +
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
                    List<String> topicName = hit.highlight.get("topicName");
                    if(topicName!=null){
                    list.add(topicName.get(0));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    //过滤条件是 正在监控的项目或在指定时间之后的项目

    //建立topic_scan表
/*    private void initTopicScanIndex(){
        String setting = "{\n" +
        "        \"index\" : {\n" +
        "            \"analysis.analyzer.default.type\": \"ik_max_word\"\n" +
        "        }\n" +
        "    }";
        CreateIndex createIndex = new CreateIndex.Builder("sinatopicscan").settings(setting).build();
        try {
            jestClient.execute(createIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

/*    private boolean indexIsExist(){
        boolean flag = false;
        Cat cat = new Cat.IndicesBuilder().addIndex("sinatopicscan").build();
        try {
            CatResult execute = jestClient.execute(cat);
            System.out.println("execute = "+execute);
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }catch (JsonSyntaxException e){
            e.printStackTrace();
        }
        return flag;
    }*/

}
