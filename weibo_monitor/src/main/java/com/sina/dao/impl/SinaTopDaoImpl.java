package com.sina.dao.impl;


import com.sina.dao.SinaTopDao;
import com.sina.pojo.SinaTopBean;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.*;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.IndicesExists;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//xsort=hot&Refer=hotmore    查看热搜下的热门话题
@Repository
public class SinaTopDaoImpl implements SinaTopDao {

    private final JestClient jestClient;
    public SinaTopDaoImpl(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    //从数据库获取数据
    /*
    * 注意关于查询：
    * 首先要明确 Elasticsearch是通过倒排索引，通过关键字进行匹配           今天天气好晴朗
    * term                      对查询的内容不分词                     含有“今天天气好晴朗”的词
    * match                     进行分词 包含一个词显示结果              “今天” “天气” “好” “晴朗”    含有之一就可显示
    * match_phrase              进行分词 所有的词必须要包含              “今天” “天气” “好” “晴朗”     所有词必须包含
    * match_phrase_prefix       从前到后进行匹配                        “今天” “天气” “好”           可以匹配    “今天”  “好” 不能匹配
    * */
    @Override
    public List<SinaTopBean> getHotFromDB(String content, int page){
        List<SinaTopBean> list = new ArrayList<>();
        String query = "{" +"\n"+
                "\"query\": {\n" +
                "    \"term\": {\n" +
                "       \"keyword\":\""+content+"\"\n" +
                "    }\n" +
                "  },\n" +
                "\"from\":"+page+",\n"+
                "  \"size\": 10000" +"\n"+
                ",\"sort\": [\n" +
                "    {\n" +
                "      \"current_date\": {\n" +
                "        \"order\": \"asc\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]"+
                "}";
        Search search = new Search.Builder(query).addIndex("sina").addType("hot").build();
        try {
            SearchResult execute = jestClient.execute(search);
            if(execute.getResponseCode() == 200){
                List<SearchResult.Hit<SinaTopBean, Void>> hits = execute.getHits(SinaTopBean.class);
                for (SearchResult.Hit<SinaTopBean, Void> hit : hits) {
                    list.add(hit.source);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    //把数据批量存储至数据库
    //我们需要使用事务bulk
    @Override
    public void storeCurrentHotData(List<SinaTopBean> list) {
        //如果是第一次访问先初始化index
/*        if(isFirst){
            initHotIndex();
        }*/
        Bulk.Builder bulk_builder = new Bulk.Builder()
                .defaultIndex("sina")
                .defaultType("hot");
        for (SinaTopBean sinaTopBean : list) {
            bulk_builder.addAction(new Index.Builder(sinaTopBean).build());
        }
        Bulk bulk = bulk_builder.build();
        try {
            jestClient.execute(bulk);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //初始化sina
    //ik分词器作用域整个database
/*    private void initHotIndex(){
        String setting = "{\n" +
                "            analysis: {\n" +
                "                analyzer: {\n" +
                "                    default: {\n" +
                "                        type: keyword\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n";
        CreateIndex createIndex = new CreateIndex.Builder("sina").settings(setting).build();
        try {
            jestClient.execute(createIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
