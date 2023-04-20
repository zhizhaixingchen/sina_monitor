package com.sina.dao.impl;

import com.sina.dao.SinaHotTopicStatDao;
import com.sina.pojo.MonitorBlogES;
import com.sina.pojo.SinaHotTopicStatBean;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Repository
public class SinaHotTopicStatDaoImpl implements SinaHotTopicStatDao {
    private JestClient jestClient;
    public SinaHotTopicStatDaoImpl(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    //插入数据
    @Override
    public void insertData(SinaHotTopicStatBean sinaHotTopicStatBean) {
        Index index = new Index.Builder(sinaHotTopicStatBean).index("sinahotstat").type("topic").build();
        try {
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SinaHotTopicStatBean> queryAll() {
        List<SinaHotTopicStatBean> list = new ArrayList<>();
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"match_all\": {\n" +
                "    }\n" +
                "  },\n" +
                "  \"sort\": [\n" +
                "    {\n" +
                "      \"current_date\": {\n" +
                "        \"order\": \"asc\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]  \n" +
                "}";
        Search build = new Search.Builder(query).addIndex("sinahotstat").addType("topic").build();
        try {
            SearchResult execute = jestClient.execute(build);
            if(execute.getResponseCode() == 200){
                List<SearchResult.Hit<SinaHotTopicStatBean, Void>> hits = execute.getHits(SinaHotTopicStatBean.class);
                for (SearchResult.Hit<SinaHotTopicStatBean, Void> hit : hits) {
                    list.add(hit.source);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
