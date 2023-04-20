package com.sina.dao.impl;

import com.sina.dao.SinaDayStat;
import com.sina.pojo.SinaHotTopicStatBean;
import com.sina.pojo.SinaTopBean;
import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SinaDayStatImpl implements SinaDayStat {
    private JestClient jestClient;
    public SinaDayStatImpl(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    @Override
    public void sinaHotTransfer() {
        List<SinaTopBean> allList = new ArrayList<>();
        int page = 0;
        List<SinaTopBean> list = getOnePageHot(page++);
        allList.addAll(list);
        while (list.size() == 10000){
            list = getOnePageHot(page++);
            allList.addAll(list);
        }

        //存储至oldsina中
        initIndex("oldsina");
        Bulk.Builder bulk_builder = new Bulk.Builder().defaultIndex("oldsina").defaultType("hot");
        for (SinaTopBean sinaTopBean : allList) {
            bulk_builder.addAction(new Index.Builder(sinaTopBean).build());
        }
        Bulk bulk = bulk_builder.build();
        try {
            jestClient.execute(bulk);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //获取一页的数据
    private List<SinaTopBean> getOnePageHot(int from) {
        List<SinaTopBean> list = new ArrayList<>();
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"match_all\": {}\n" +
                "    \n" +
                "  },\n" +
                "  \"from\": "+from+",\n" +
                "  \"size\": 10000\n" +
                "}";
        //获取sina表中所有数据
        Search builder = new Search.Builder(query).addIndex("sina").addType("hot").build();
        try {
            SearchResult execute = jestClient.execute(builder);
            if (execute.getResponseCode() == 200) {
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

    //先删除index sina 在创建sina
    @Override
    public void sinaHotclear() {
        DeleteIndex sina = new DeleteIndex.Builder("sina").build();
        try {
            jestClient.execute(sina);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initIndex("sina");
    }

    @Override
    public void sinaHotStatTransfer() {
        //获取sinahotstat表中所有数据
        List<SinaHotTopicStatBean> list = new ArrayList<>();
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"match_all\": {}\n" +
                "    \n" +
                "  },\n" +
                "  \"from\": 0,\n" +
                "  \"size\": 10000\n" +
                "}";
        //获取sina表中所有数据
        Search builder = new Search.Builder(query).addIndex("sinahotstat").addType("topic").build();
        try {
            SearchResult execute = jestClient.execute(builder);
            if(execute.getResponseCode()==200){
                List<SearchResult.Hit<SinaHotTopicStatBean, Void>> hits = execute.getHits(SinaHotTopicStatBean.class);
                for (SearchResult.Hit<SinaHotTopicStatBean, Void> hit : hits) {
                    list.add(hit.source);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //默认的表结构就可以，不需要初始化表
        Bulk.Builder bulk_builder = new Bulk.Builder().defaultIndex("oldsinahotstat").defaultType("topic");
        for (SinaHotTopicStatBean topicStatBean : list) {
            bulk_builder.addAction(new Index.Builder(topicStatBean).build());
        }
        Bulk bulk = bulk_builder.build();
        try {
            jestClient.execute(bulk);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取一页的数据   sinahotstat一分钟添加一条数据 一天一共添加24*60 = 1440条数据，我们可以一次读取完毕   批量获取舍弃
    /*private List<SinaHotTopicStatBean> getOnePageHotStat(int from) {
        List<SinaHotTopicStatBean> list = new ArrayList<>();
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"match_all\": {}\n" +
                "    \n" +
                "  },\n" +
                "  \"from\": "+from+",\n" +
                "  \"size\": 10000\n" +
                "}";
        //获取sina表中所有数据
        Search builder = new Search.Builder(query).addIndex("sinahotstat").addType("topic").build();
        try {
            SearchResult execute = jestClient.execute(builder);
            if(execute.getResponseCode()==200){
                List<SearchResult.Hit<SinaHotTopicStatBean, Void>> hits = execute.getHits(SinaHotTopicStatBean.class);
                for (SearchResult.Hit<SinaHotTopicStatBean, Void> hit : hits) {
                    list.add(hit.source);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }*/

    @Override
    public void sinaHotStatClear() {
        DeleteIndex sina = new DeleteIndex.Builder("sinahotstat").build();
        try {
            jestClient.execute(sina);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //使用初始的表结构就行 删除表之后 不再进行新建操作
    }

    @Override
    public void usersClear() {
        DeleteIndex sina = new DeleteIndex.Builder("users").build();
        try {
            jestClient.execute(sina);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //初始化sina与oldsina
    private void initIndex(String index){
        String setting = "{\n" +
                "            analysis: {\n" +
                "                analyzer: {\n" +
                "                    default: {\n" +
                "                        type: keyword\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n";
        CreateIndex createIndex = new CreateIndex.Builder(index).settings(setting).build();
        try {
            jestClient.execute(createIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
