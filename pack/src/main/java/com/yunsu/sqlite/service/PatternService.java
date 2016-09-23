package com.yunsu.sqlite.service;

import com.yunsu.greendao.entity.PatternInfo;

import java.util.List;

/**
 * Created by yunsu on 2016/9/23.
 */
public interface PatternService {

    long insert(PatternInfo patternInfo);

    void delete(PatternInfo patternInfo);

    void update(PatternInfo patternInfo);

    List<PatternInfo> queryAllPatternInfo();

}
