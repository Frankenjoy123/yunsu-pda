package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class PackDaoMaker {
    public static void main(String[] args) {
        // 正如你所见的，你创建了一个用于添加实体（Entity）的模式（Schema）对象。
        // 两个参数分别代表：数据库版本号与自动生成代码的包路径。
        Schema schema = new Schema(1, "com.yunsu.greendao.entity");
        addPackAndProduct(schema);
        //指定dao
        schema.setDefaultJavaPackageDao("com.yunsu.greendao.dao");
        try {
            //指定路径
            new DaoGenerator().generateAll(schema, "C:\\XQS\\yunsu-pda\\pack\\src\\main\\java-gen");
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
        Entity pack = schema.addEntity("Pack");
        //主键
        pack.addIdProperty().autoincrement();
        //名称
        pack.addStringProperty("packKey").notNull();;
        //年龄
        pack.addStringProperty("status");
        //地址
        pack.addDateProperty("lastSaveTime");


        //创建数据库的表
        Entity product = schema.addEntity("Product");
        product.addIdProperty().autoincrement();
        //名称
        product.addStringProperty("productKey");
        //年龄
        product.addStringProperty("status");
        //地址
        product.addDateProperty("lastSaveTime");

        //建立一对多关联
        Property packId=product.addLongProperty("packId").getProperty();
        product.addToOne(pack,packId);

        ToMany packToProducts=pack.addToMany(product,packId);
        packToProducts.setName("products");


        //员工编号
        Entity staff =schema.addEntity("Staff");
        staff.addIdProperty().autoincrement();
        staff.addStringProperty("staffNumber");
        staff.addStringProperty("name");

        //产品信息表
        Entity productBase=schema.addEntity("ProductBase");
        productBase.addIdProperty().autoincrement();
        productBase.addStringProperty("productNumber");
        productBase.addStringProperty("name");

    }
}