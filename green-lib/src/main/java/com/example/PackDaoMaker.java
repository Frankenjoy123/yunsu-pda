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
        //包装码
        pack.addStringProperty("packKey").notNull();;
        //同步状态
        pack.addStringProperty("status");
        //最近操作时间
        pack.addStringProperty("lastSaveTime");
        //规格
        pack.addIntProperty("standard");
        //实际产品数
        pack.addIntProperty("realCount");

        //创建数据库的表
        Entity product = schema.addEntity("Product");
        product.addIdProperty().autoincrement();
        product.addStringProperty("productKey");
        product.addStringProperty("status");
        product.addStringProperty("lastSaveTime");

        //建立包装和产品的一对多关联
        Property packId=product.addLongProperty("packId").getProperty();
        product.addToOne(pack,packId);

        ToMany packToProducts=pack.addToMany(product,packId);
        packToProducts.setName("products");


        //员工编号
        Entity staff =schema.addEntity("Staff");
        staff.addIdProperty().autoincrement();
        staff.addStringProperty("staffNumber");
        staff.addStringProperty("name");

        //建立员工和包装的一对多关联
        Property staffId=pack.addLongProperty("staffId").getProperty();
        pack.addToOne(staff,staffId);

        //产品信息表
        Entity productBase=schema.addEntity("ProductBase");
        productBase.addIdProperty().autoincrement();
        productBase.addStringProperty("productNumber");
        productBase.addStringProperty("name");


        //建立产品信息和包装的一对多关联
        Property productBaseId=pack.addLongProperty("productBaseId").getProperty();
        pack.addToOne(productBase,productBaseId);

    }
}