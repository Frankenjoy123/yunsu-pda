package com.yunsoo.sqlite.service.impl;

import com.yunsoo.manager.GreenDaoManager;
import com.yunsoo.sqlite.service.OrderService;
import com.yunsu.greendao.dao.OrderDao;
import com.yunsu.greendao.entity.Material;
import com.yunsu.greendao.entity.Order;

import java.util.List;

import de.greenrobot.daogenerator.Query;

/**
 * Created by xiaowu on 2016/8/16.
 */
public  class OrderServiceImpl implements OrderService{
    private OrderDao orderDao=GreenDaoManager.getInstance().getDaoSession().getOrderDao();

    @Override
    public void insertOrder(Order order) {
        orderDao.insert(order);
    }

    @Override
    public void updatePack(Order order) {
        orderDao.update(order);
    }

    @Override
    public Order queryByOrderNumber(String orderNumber) {
        return orderDao.queryBuilder().
                where(OrderDao.Properties.OrderNumber.eq(orderNumber)).unique();
    }

    @Override
    public void deleteOrder(Order order) {
        orderDao.delete(order);
    }

    @Override
    public List<Material> queryPacks(Order order) {
        Order resultOrder=orderDao.queryBuilder().
                where(OrderDao.Properties.OrderNumber.eq(order.getOrderNumber())).build().unique();
        return resultOrder.getMaterials();
    }
}
