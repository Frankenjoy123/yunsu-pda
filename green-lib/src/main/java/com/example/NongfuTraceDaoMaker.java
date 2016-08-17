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
        addTable(schema);
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
    public static void addTable(Schema schema) {

        //创建订单表
        Entity order = schema.addEntity("Order");
        //主键
        order.addIdProperty().autoincrement();
        //订单编号
        order.addStringProperty("orderNumber");
        //客户名称
        order.addStringProperty("customerName");
        //收货联系人
        order.addStringProperty("consignee");
        //联系电话
        order.addStringProperty("contactPhone");
        //送货地址
        order.addStringProperty("deliveryAddress");
        //运输代理
        order.addStringProperty("transportationAgent");
        //车牌号
        order.addStringProperty("licensePlateNumber");
        //承运人
        order.addStringProperty("carrierName");
        //承运人电话
        order.addStringProperty("carrierPhone");
        //承运人身份证号
        order.addStringProperty("carrierIdNumber");
        //发货仓库
        order.addStringProperty("deliveryWarehouse");
        //订单状态
        order.addStringProperty("status");
        //发货时间
        order.addStringProperty("deliveryTime");


        //创建物料表
        Entity material=schema.addEntity("Material");
        material.addIdProperty().autoincrement();
        //物料号
        material.addStringProperty("materialNumber");
        //头数
        material.addStringProperty("headSize");
        //级别
        material.addStringProperty("level");
        //净重
        material.addStringProperty("netWeight");
        //完成状态
        material.addStringProperty("status");
        //时间
        material.addStringProperty("time");
        //总量
        material.addLongProperty("amount");
        //已发货
        material.addLongProperty("sent");
        //剩余
        material.addLongProperty("remain");

        //建立一对多关联
        Property orderId=material.addLongProperty("orderId").getProperty();
        material.addToOne(order,orderId);

        ToMany orderToMaterials=order.addToMany(material,orderId);
        orderToMaterials.setName("materials");


        //创建数据库的表
        Entity pack = schema.addEntity("Pack");
        //主键
        pack.addIdProperty().autoincrement();
        //包装码
        pack.addStringProperty("packKey").notNull();
        //action
        pack.addStringProperty("actionId");
        //经销商
        pack.addStringProperty("agency");
        //状态
        pack.addStringProperty("status");
        //时间
        pack.addStringProperty("saveTime");
        //默认1，按垛出库
        pack.addIntProperty("count");
        //类型，按垛或按箱
        pack.addStringProperty("type");


        //建立一对多关联
        Property materialId=pack.addLongProperty("materialId").getProperty();
        pack.addToOne(material,materialId);

        ToMany materialToPacks=material.addToMany(pack,materialId);
        materialToPacks.setName("packs");
    }
}