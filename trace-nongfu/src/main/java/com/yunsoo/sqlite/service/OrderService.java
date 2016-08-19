package com.yunsu.sqlite.service;

import com.yunsu.greendao.entity.Material;
import com.yunsu.greendao.entity.Order;
import java.util.List;

/**
 * Created by yunsu on 2016/8/8.
 */
public interface OrderService {
    //插入订单
    void insertOrder(Order order);

    //更新订单
    void updatePack(Order order);

    //根据订单编号查询订单
    Order queryByOrderNumber(String orderNumber);

    //删除订单
    void deleteOrder(Order order);

    //查询订单中的所有物料单
    public List<Material> queryPacks(Order order);

}
