package com.sina.dao.impl;

import com.sina.dao.SinaMonthStat;
import com.sina.dbConfig.HiveJdbcClient;
import io.searchbox.client.JestClient;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Repository
public class SinaMonthStatImpl implements SinaMonthStat {
    private JestClient jestClient;
    private HiveJdbcClient hiveJdbcClient;
    private static String year;
    private static String month;

    //我们在每一个月1日凌晨进行数据统计。
    //上一个月的计算问题     当前时间5小时之前是上个月 取出年和月
    static {
        long now = System.currentTimeMillis();
        //一小时之前
        long beforeNow = now - 1000*60*60*5;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String before = sdf.format(new Date(beforeNow));
        year = before.split("-")[0];
        month = before.split("-")[1];
    }
    public SinaMonthStatImpl(JestClient jestClient, HiveJdbcClient hiveJdbcClient) {
        this.jestClient = jestClient;
        this.hiveJdbcClient = hiveJdbcClient;
    }

//hive部分
    //存储热门数据
    @Override
    public void oldSinaStore() {
        //hive jdbc对象
        JdbcTemplate jdbcTemplate = hiveJdbcClient.getJdbcTemplate();
        //创建临时表外表
        String externalHotSql = "create external table if not exists tmp_sinahot(\n" +
                "  `current_date` bigint, \n" +
                "  `stage` bigint, \n" +
                "  `keyword` string, \n" +
                "  `access_count` bigint, \n" +
                "  `emotion_point` float, \n" +
                "  `link` string, \n" +
                "  `words` array<string>\n" +
                ") \n" +
                "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t'\n" +
                "COLLECTION ITEMS TERMINATED BY ','\n" +
                "stored by 'org.elasticsearch.hadoop.hive.EsStorageHandler'\n" +
                "TBLPROPERTIES(\n" +
                "'es.nodes' = '192.168.52.113:9200',\n" +
                "'es.index.auto.create' = 'false',\n" +
                "'es.resource' = 'oldsina/hot',\n" +
                "'es.read.metadata' = 'true'\n" +
                ")";
        jdbcTemplate.execute(externalHotSql);

        //创建内表  内表已经创建
        //向内表中插入数据
        String transferToInner = "insert into table old_sinahot partition(year='"+year+"',month='"+month+"') select * from tmp_sinahot";
        try {
            jdbcTemplate.update(transferToInner);
        }catch (DataAccessResourceFailureException e){
            System.out.println("sinahot未检测到数据");
        }

        //删除临时表外表
        String deleteExternalSql = "drop table tmp_sinahot";
        jdbcTemplate.execute(deleteExternalSql);
    }

    //存储热门数据统计信息
    @Override
    public void old_sinahotstatStore() {
        //hive对象 jdbc对象
        JdbcTemplate jdbcTemplate = hiveJdbcClient.getJdbcTemplate();
        //创建临时表外表
        String externalHotStatSql = "create external table if not exists tmp_sinahotstat(\n" +
                "  `current_date` bigint, \n" +
                "  `emotion` float, \n" +
                "  `hot` bigint\n" +
                ") \n" +
                "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t'\n" +
                "stored by 'org.elasticsearch.hadoop.hive.EsStorageHandler'\n" +
                "TBLPROPERTIES(\n" +
                "'es.nodes' = '192.168.52.113:9200',\n" +
                "'es.index.auto.create' = 'false',\n" +
                "'es.resource' = 'oldsinahotstat/topic',\n" +
                "'es.read.metadata' = 'true'\n" +
                ")";
        jdbcTemplate.execute(externalHotStatSql);

        //向内表中插入数据
        String transferToInner = "insert into table old_sinahotstat partition(year='"+year+"',month='"+month+"') select * from tmp_sinahotstat";
        try {
            jdbcTemplate.update(transferToInner);
        }catch (DataAccessResourceFailureException e){
            System.out.println("sinahotstat未检测到数据");
        }

        //删除临时表外表
        String deleteExternalSql = "drop table tmp_sinahotstat";
        jdbcTemplate.execute(deleteExternalSql);
    }

    //监控话题存储
    @Override
    public void sinaTopicStore() {
        JdbcTemplate jdbcTemplate = hiveJdbcClient.getJdbcTemplate();
        //创建临时表外部表
        String externalSql = "create external table if not exists tmp_sinatopic(\n" +
                "  `current_date` bigint, \n" +
                "  `imgurl` string, \n" +
                "  `readnum` string, \n" +
                "  `discuss` string, \n" +
                "  `keyword` string, \n" +
                "  `finalemotionstage` float\n" +
                ") \n" +
                "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t'\n" +
                "STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'\n" +
                "TBLPROPERTIES(\n" +
                "'es.nodes' = '192.168.52.113:9200',\n" +
                "'es.index.auto.create' = 'false',\n" +
                "'es.resource' = 'sinatopic/topic',\n" +
                "'es.read.metadata' = 'true',\n" +
                "'es.mapping.names' = 'imgurl:imgUrl, readnum:readNum, finalemotionstage:finalEmotionStage'\n" +
                ")";
        jdbcTemplate.execute(externalSql);

        //向内部表中插入数据
        String transferToInner = "insert into table old_sinatopic partition(year='"+year+"',month='"+month+"') select * from tmp_sinatopic";
        try {
            jdbcTemplate.update(transferToInner);
        }catch (DataAccessResourceFailureException e){
            System.out.println("sinatopic未检测到数据");
        }

        //删除外部临时表
        String deleteExternalSql = "drop table tmp_sinatopic";
        jdbcTemplate.execute(deleteExternalSql);
    }

    @Override
    public void blogsStore() {
        JdbcTemplate jdbcTemplate = hiveJdbcClient.getJdbcTemplate();
        String externalBlogSql = "create external table if not exists tmp_blogs(\n" +
                "  `current_date` bigint,\n" +
                "  `mid` bigint, \n" +
                "  `uid` bigint, \n" +
                "  `transfer` bigint, \n" +
                "  `comments` bigint, \n" +
                "  `like` bigint, \n" +
                "  `totalemotion` float\n" +
                ") \n" +
                "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t'\n" +
                "STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'\n" +
                "TBLPROPERTIES(\n" +
                "'es.nodes' = '192.168.52.113:9200',\n" +
                "'es.index.auto.create' = 'false',\n" +
                "'es.resource' = 'blogs/blogdata',\n" +
                "'es.read.metadata' = 'true',\n" +
                "'es.mapping.names' = 'totalemotion:totalEmotion'\n" +
                ")";
        jdbcTemplate.execute(externalBlogSql);

        //向内表中插入数据
        String transferToInner = "insert into table old_blogs partition(year='"+year+"',month='"+month+"') select * from tmp_blogs";
        try {
            jdbcTemplate.update(transferToInner);
        }catch (DataAccessResourceFailureException e){
            System.out.println("blogs未检测到数据");
        }

        //删除临时表
        String deleteExternalSql = "drop table tmp_blogs";
        jdbcTemplate.execute(deleteExternalSql);
    }

//elasticsearch

    @Override
    public void initOldSina() {
        //删除旧表
        DeleteIndex sina = new DeleteIndex.Builder("oldsina").build();
        try {
            jestClient.execute(sina);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //添加新表
        initIndex("oldsina");
    }

    @Override
    public void initOld_sinahotstat() {
        //删除旧表
        DeleteIndex sina = new DeleteIndex.Builder("oldsinahotstat").build();
        try {
            jestClient.execute(sina);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //新表不需要添加 我们使用默认的就可以
    }

    @Override
    public void initSinaTopicStore() {
        DeleteIndex sina = new DeleteIndex.Builder("sinatopic").build();
        try {
            jestClient.execute(sina);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //初始化新表
        initIndex("sinatopic");
    }

    @Override
    public void initBlogs() {
        DeleteIndex sina = new DeleteIndex.Builder("blogs").build();
        try {
            jestClient.execute(sina);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //新表不需要添加 我们使用默认的就可以

    }


    /*
    @Override
    public void initSinaTopicbeanScan() {
        //删除搜索表
        DeleteIndex sina = new DeleteIndex.Builder("sinatopicscan").build();
        try {
            jestClient.execute(sina);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //使用ik分词 与其它的不同
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

    //初始化index
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
