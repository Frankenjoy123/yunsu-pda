package com.yunsu.sqlite.service.impl;

import com.yunsu.greendao.dao.StaffDao;
import com.yunsu.greendao.entity.Staff;
import com.yunsu.manager.GreenDaoManager;
import com.yunsu.sqlite.service.StaffService;

import java.util.List;

/**
 * Created by yunsu on 2016/9/8.
 */
public class StaffServiceImpl implements StaffService {

    StaffDao staffDao= GreenDaoManager.getInstance()
            .getDaoSession().getStaffDao();

    @Override
    public long insert(Staff staff) {
        return staffDao.insert(staff);
    }

    @Override
    public void delete(Staff staff) {
        staffDao.delete(staff);
    }

    @Override
    public void update(Staff staff) {
        staffDao.update(staff);
    }

    @Override
    public List<Staff> queryAllStaff() {
        return staffDao.queryBuilder().list();
    }

    @Override
    public Staff queryStaffById(long id) {
        return staffDao.queryBuilder()
                .where(StaffDao.Properties.Id.eq(id)).unique();
    }

}
