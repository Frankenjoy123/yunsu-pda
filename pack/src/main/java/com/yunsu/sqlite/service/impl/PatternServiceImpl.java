package com.yunsu.sqlite.service.impl;

import com.yunsu.greendao.dao.PatternInfoDao;
import com.yunsu.greendao.entity.PatternInfo;
import com.yunsu.manager.GreenDaoManager;
import com.yunsu.sqlite.service.PatternService;

import java.util.List;


/**
 * Created by yunsu on 2016/9/23.
 */
public class PatternServiceImpl implements PatternService {
    private PatternInfoDao patternDao= GreenDaoManager.getInstance().getDaoSession().getPatternInfoDao();

    @Override
    public long insert(PatternInfo patternInfo) {
        return patternDao.insert(patternInfo);
    }

    @Override
    public void delete(PatternInfo patternInfo) {
        patternDao.delete(patternInfo);
    }

    @Override
    public void update(PatternInfo patternInfo) {
        patternDao.update(patternInfo);
    }

    @Override
    public List<PatternInfo> queryAllPatternInfo() {
        return patternDao.queryBuilder().list();
    }

    @Override
    public PatternInfo queryPatternById(long id) {
        return patternDao.queryBuilder()
                .where(PatternInfoDao.Properties.Id.eq(id)).unique();
    }
}
