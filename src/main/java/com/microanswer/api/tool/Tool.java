package com.microanswer.api.tool;

import com.jfinal.plugin.activerecord.Db;

import java.util.List;

/**
 * Created by Microanswer on 2017/7/6.
 */
public class Tool {
    // 查询是否存在某一张表
    public static boolean hasTable(String name) {
        List<Object> query = Db.query("SELECT count(*) FROM sqlite_master WHERE type='table' and name='" + name + "';");
        return !(query == null || query.size() != 1) && ((Integer) query.get(0)) == 1;
    }
}
