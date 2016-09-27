package com.yunsu.sqlite.service;

import com.yunsu.greendao.entity.Staff;

import java.util.List;

/**
 * Created by yunsu on 2016/9/8.
 */
public interface StaffService {

    long insert(Staff staff);

    void delete(Staff staff);

    void update(Staff staff);

    List<Staff> queryAllStaff();

    Staff queryStaffById(long id);

    boolean existPackDataByStaffId(long staffId);

}
