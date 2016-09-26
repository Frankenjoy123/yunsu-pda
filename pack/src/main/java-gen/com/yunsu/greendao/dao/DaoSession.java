package com.yunsu.greendao.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.yunsu.greendao.entity.Pack;
import com.yunsu.greendao.entity.Product;
import com.yunsu.greendao.entity.Staff;
import com.yunsu.greendao.entity.ProductBase;
import com.yunsu.greendao.entity.PatternInfo;

import com.yunsu.greendao.dao.PackDao;
import com.yunsu.greendao.dao.ProductDao;
import com.yunsu.greendao.dao.StaffDao;
import com.yunsu.greendao.dao.ProductBaseDao;
import com.yunsu.greendao.dao.PatternInfoDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig packDaoConfig;
    private final DaoConfig productDaoConfig;
    private final DaoConfig staffDaoConfig;
    private final DaoConfig productBaseDaoConfig;
    private final DaoConfig patternInfoDaoConfig;

    private final PackDao packDao;
    private final ProductDao productDao;
    private final StaffDao staffDao;
    private final ProductBaseDao productBaseDao;
    private final PatternInfoDao patternInfoDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        packDaoConfig = daoConfigMap.get(PackDao.class).clone();
        packDaoConfig.initIdentityScope(type);

        productDaoConfig = daoConfigMap.get(ProductDao.class).clone();
        productDaoConfig.initIdentityScope(type);

        staffDaoConfig = daoConfigMap.get(StaffDao.class).clone();
        staffDaoConfig.initIdentityScope(type);

        productBaseDaoConfig = daoConfigMap.get(ProductBaseDao.class).clone();
        productBaseDaoConfig.initIdentityScope(type);

        patternInfoDaoConfig = daoConfigMap.get(PatternInfoDao.class).clone();
        patternInfoDaoConfig.initIdentityScope(type);

        packDao = new PackDao(packDaoConfig, this);
        productDao = new ProductDao(productDaoConfig, this);
        staffDao = new StaffDao(staffDaoConfig, this);
        productBaseDao = new ProductBaseDao(productBaseDaoConfig, this);
        patternInfoDao = new PatternInfoDao(patternInfoDaoConfig, this);

        registerDao(Pack.class, packDao);
        registerDao(Product.class, productDao);
        registerDao(Staff.class, staffDao);
        registerDao(ProductBase.class, productBaseDao);
        registerDao(PatternInfo.class, patternInfoDao);
    }
    
    public void clear() {
        packDaoConfig.getIdentityScope().clear();
        productDaoConfig.getIdentityScope().clear();
        staffDaoConfig.getIdentityScope().clear();
        productBaseDaoConfig.getIdentityScope().clear();
        patternInfoDaoConfig.getIdentityScope().clear();
    }

    public PackDao getPackDao() {
        return packDao;
    }

    public ProductDao getProductDao() {
        return productDao;
    }

    public StaffDao getStaffDao() {
        return staffDao;
    }

    public ProductBaseDao getProductBaseDao() {
        return productBaseDao;
    }

    public PatternInfoDao getPatternInfoDao() {
        return patternInfoDao;
    }

}
