package com.yunsu.sqlite.service.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    private SQLiteDatabase db=GreenDaoManager.getInstance().getDb();

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

    @Override
    public boolean existPackDataByStaffId(long staffId) {
        StringBuilder builder=new StringBuilder();
        builder.append("select count(*)");
        builder.append("from Pack p inner join staff s on p.staff_id = s._id ");
        builder.append("where p.staff_id=?");

        Cursor c=db.rawQuery(builder.toString(),new String[]{String.valueOf(staffId)});

        if (c!=null){
            c.moveToFirst();
            int count = c.getInt(0);
            if (count>0){
                return true;
            }
        }
        return false;
    }

}
