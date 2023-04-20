package com.sina.dao.mapper;

import com.sina.pojo.Loginuser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginuserMapper {

    Loginuser queryUserBymail(@Param("mail")String mail);
    //查询所有用户  除了超级管理员(最终)
    List<Loginuser> queryAll(@Param("mail")String exceptmail);

    void insertUser(Loginuser loginuser);

    void updateLock(@Param("mail")String mail,@Param("lock")int lock,@Param("log")String log);

    void updateAllLock();

    void updatePower(@Param("mail")String mail, @Param("field")String field, @Param("status") byte status);

    void updateAllPower(@Param("mail")String mail);

    void deleteUser(String mail);
}
