package com.yunsu.strategy;

import java.util.List;

/**
 * Created by xiaowu on 2016/10/17.
 */
public interface PackProductStringStrategy {
    String generatePackProductString(String packString , List<String> productStringList);
}
