package com.sina.dao.impl;

import com.sina.dao.SinaUserStatDao;
import com.sina.pojo.UserBlogES;
import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SinaUserStatDaoImpl implements SinaUserStatDao {
    private JestClient jestClient;
    public SinaUserStatDaoImpl(JestClient jestClient) {
        this.jestClient = jestClient;
    }

     @Override
    public List<UserBlogES> queryByField(String field, String rotation, int from, int size) {
         String query = "{\n" +
                 "    \"query\": {\n" +
                 "      \"match_all\": {}\n" +
                 "    },\n" +
                 "    \"sort\": [\n" +
                 "      {\n" +
                 "        \""+field+"\": {\n" +
                 "          \"order\": \""+rotation+"\"\n" +
                 "        }\n" +
                 "      }\n" +
                 "    ],\n" +
                 "    \"from\": "+from+",\n" +
                 "    \"size\": "+size+"\n" +
                 "}";
         //查询语法进行封装
         List<UserBlogES> list = getBlogES(query);
         return list;
    }

    @Override
    public List<UserBlogES> queryByname(String name, int from, int size) {
        String query = "{\n" +
                "    \"query\": {\n" +
                "      \"match_phrase\": {\n" +
                "        \"name\": \""+name+"\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"sort\": [\n" +
                "      {\n" +
                "        \"current_date\": {\n" +
                "          \"order\": \"desc\"\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"from\": "+from+",\n" +
                "    \"size\": "+size+"\n" +
                "}";
        //查询语法进行封装
        List<UserBlogES> list = getBlogES(query);
        return list;
    }

    @Override
    public List<UserBlogES> queryByGroup(List<Long> uidList, int from, int size) {
        String querybefore = "{\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"should\": [\n";
        String queryMid = "";
        for (long uidStr : uidList) {
            queryMid +="{\"match_phrase\": {\"uid\": "+uidStr+"}},";
        }
        //删除最后一个逗号
        queryMid = queryMid.substring(0,queryMid.length()-1);
        String queryAfter =
                "]\n" +
                "    }\n" +
                "  },\n" +
                "  \"sort\": [\n" +
                "    {\n" +
                "      \"current_date\": {\n" +
                "        \"order\": \"desc\"\n" +
                "      }\n" +
                "    }\n" +
                "  ], \n" +
                "  \"from\": "+from+",\n" +
                "  \"size\": "+size+"\n" +
                "}";
        //查询语法进行封装
        String query = querybefore+queryMid+queryAfter;
        List<UserBlogES> list = getBlogES(query);
        return list;
    }

    //我们不需要对该表进行分词处理，因此使用自动生成的就可以
    //该表    index users  type blog
    @Override
    public void insertData(List<UserBlogES> userBlogESList) {
        Bulk.Builder bulk_builder = new Bulk.Builder()
                .defaultIndex("users")
                .defaultType("blog");
        for (UserBlogES userBlogES : userBlogESList) {
            bulk_builder.addAction(new Index.Builder(userBlogES).build());
        }
        Bulk bulk = bulk_builder.build();
        try {
            jestClient.execute(bulk);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void insertOne(UserBlogES singleBlog) {
        //注意：一个index只能存在一个type
        Index index = new Index.Builder(singleBlog).index("users").type("blog").build();
        try {
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<UserBlogES> getBlogES(String query) {
        List<UserBlogES> list = new ArrayList<>();
        Search build = new Search.Builder(query).addIndex("users").addType("blog").build();
        try {
            SearchResult execute = jestClient.execute(build);
            if (execute.getResponseCode() == 200) {
                List<SearchResult.Hit<UserBlogES, Void>> hits = execute.getHits(UserBlogES.class);
                for (SearchResult.Hit<UserBlogES, Void> hit : hits) {
                    list.add(hit.source);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    //设置不分词 严格匹配
    /*{
        "mappings": {
        "doc": {
            "properties": {
                "name": {
                    "type":  "text",
                            "index": "not_analyzed"
                }
            }
        }
    }
    }*/
}
