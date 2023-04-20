package com.sina.dao;

//每一天的统计
/*
 	sina		数据转移至old_sina		清空sina
	sinahotstat	数据转移至old_sinahotstat	清空sinahotstat
	users					清空users
*/

public interface SinaDayStat{
    void sinaHotTransfer();
    void sinaHotclear();
    void sinaHotStatTransfer();
    void sinaHotStatClear();
    void usersClear();
}
