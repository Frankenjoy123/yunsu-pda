package com.yunsu.sqlite.service;

import com.yunsu.greendao.entity.Express;

/**
 * Created by yunsu on 2016/7/27.
 */
public interface ExpressService {
    /*
     增加
     */
    long addExpress(Express express);

    /*
    根据包装码查询
     */
     Express QueryExpressByPackKey(String packKey);

     Express QueryExpressByExpressKey(String expressKey);

    //根据日期查询快递数
    long queryExpressCount(String date);

}
