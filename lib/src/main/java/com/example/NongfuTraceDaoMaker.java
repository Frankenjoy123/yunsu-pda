package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class NongfuTraceDaoMaker {
    public static void main(String[] args) {
        // 正如你所见的，你创建了一个用于添加实体（Entity）的模式（Schema）对象。
        // 两个参数分别代表：数据库版本号与自动生成代码的包路径。
        Schema schema = new Schema(1, "com.yunsu.greendao.entity");
        addPackAndProduct(schema);
        //指定dao
        schema.setDefaultJavaPackageDao("com.yunsu.greendao.dao");
        try {
            //指定路径
            new DaoGenerator().generateAll(schema, "C:\\XQS\\yunsu-pda\\trace-nongfu\\src\\main\\java-gen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建数据库的表
     *
     * @param schema
     */
    public static void addPackAndProduct(Schema schema) {

        //创建数据库的表
        Entity pack = schema.addEntity("OutBound");
        //主键
        pack.addIdProperty().autoincrement();
        //包装码
        pack.addStringProperty("packKey").notNull();;
        //状态
        pack.addStringProperty("status");
        //时间
        pack.addDateProperty("saveTime");
        //默认1，按垛出库
        pack.addIntProperty("count");
    }
}