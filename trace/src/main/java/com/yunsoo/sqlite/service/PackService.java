package com.yunsoo.sqlite.service;

import com.yunsoo.entity.OrgAgency;
import com.yunsu.greendao.entity.Pack;
import java.util.List;
import java.util.Map;

/**
 * Created by yunsu on 2016/8/8.
 */
public interface PackService {
    //插入一条物流数据
    void insertPackData(Pack pack);

    //插入时检查是否存在，如果存在直接更新
    void insertPackWithCheck(Pack pack);

    //更新物流包装
    void updatePack(Pack pack);

    //撤销一条记录
    void revokePathData(Pack pack);

    //根据Key,actionid ，status查询
    Pack queryByKeyActionStatus(Pack pack);

    //根据Key,actionId查询
    Pack queryByKeyAction(Pack pack);

    //根据Action和status查询Pack List
    List<Pack> queryPackListByActionStatus(Pack pack,int offset);

    //根据Action，经销商和status查询Pack List
    List<Pack> queryPackKeyByActionAgencyStatus(Pack pack,int offset);

    //批量更新状态
    void batchUpdateStatus(List<Pack> packList,String status);


    //查询action
    List<String> queryDistinctAction();

    //查询经销商
    List<String> queryDistinctAgency();

    //根据日期查询经销商的出库计数
    Map<String,OrgAgency> queryOrgAgentCount(String date);

    //根据日期查询出入库的总数
    Map<String,Integer>  queryInOutCount(String date);
}
