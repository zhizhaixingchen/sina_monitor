package com.sina.dao.impl;

import com.sina.dao.SinaMonitorBlogDao;
import com.sina.pojo.MonitorBlogES;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SinaMonitorBlogDaoImpl implements SinaMonitorBlogDao {

    private JestClient jestClient;
    public SinaMonitorBlogDaoImpl(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    @Override
    public List<MonitorBlogES> queryByMid(long mid,int page) {
        List<MonitorBlogES> list = new ArrayList<>();
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"match_phrase\": {\n" +
                "      \"mid\": "+mid+"\n" +
                "    }\n" +
                "  },\n" +
                "  \"sort\": [\n" +
                "    {\n" +
                "      \"current_date\": {\n" +
                "        \"order\": \"asc\"\n" +
                "      }\n" +
                "    }\n" +
                "  ],  \n" +
                "  \"from\": "+page+",\n" +
                "  \"size\": 10000\n" +
                "}";
        Search build = new Search.Builder(query).addIndex("blogs").addType("blogdata").build();
        try {
            SearchResult execute = jestClient.execute(build);
            if(execute.getResponseCode() == 200){
                List<SearchResult.Hit<MonitorBlogES, Void>> hits = execute.getHits(MonitorBlogES.class);
                for (SearchResult.Hit<MonitorBlogES, Void> hit : hits) {
                    list.add(hit.source);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void insertData(MonitorBlogES monitorBlogES) {
        Index index = new Index.Builder(monitorBlogES).index("blogs").type("blogdata").build();
        try {
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
