package com.sina.service.impl;

import com.sina.dao.SinaTopDao;
import com.sina.pojo.SinaTopBean;
import com.sina.service.SinaTopService;
import com.sina.util.sina.SinaHotTop;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

//判断是否需要将存储至数据库
@Service
public class SinaTopServiceimpl implements SinaTopService {

    private final SinaTopDao sinaTopDao;
    public SinaTopServiceimpl(SinaTopDao sinaTopDao) {
        this.sinaTopDao = sinaTopDao;
    }


    @Override
    public List<SinaTopBean> getSinaTopFromInet() {
        List<SinaTopBean> sinaHotTop = SinaHotTop.getSinaHotTop();
        SinaTopBean firstBean = sinaHotTop.get(0);
        //第一条的热度与最后一条相等
        firstBean.setAccess_count(sinaHotTop.get(sinaHotTop.size()-1).getAccess_count());

        return sinaHotTop;
    }

    @Override
    public List<SinaTopBean> getSinaTopFromDB(String content) {
        List<SinaTopBean> total_fromDB = new ArrayList<>();
        int page = 0;
        List<SinaTopBean> hotFromDB = sinaTopDao.getHotFromDB(content, 10000*page++);
        total_fromDB.addAll(hotFromDB);
        //对数据进行分页获取
        while(hotFromDB.size()==10000){
            hotFromDB = sinaTopDao.getHotFromDB(content, 10000*page++);
            total_fromDB.addAll(hotFromDB);
        }
        return total_fromDB;
    }


    @Override
    public void test() {
    }
}
