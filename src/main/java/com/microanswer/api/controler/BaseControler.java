package com.microanswer.api.controler;


import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;
import com.microanswer.api.intercept.ExceptionIntercept;
import com.microanswer.api.tool.Tool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 所有控制器的父类
 * Created by Microanswer on 2017/6/13.
 */
@Before(ExceptionIntercept.class)
public class BaseControler extends Controller {
    public static final int SUCCESS = 200;
    public static final int FAIL = 500;

    /**
     * 子类的方法中使用该方法返回数据到客户端,方便快捷2
     */
    public void answer(final int code, final String msg, final Object data, Object... detalMsg) {
        if (code != 200 && code != 500) {
            throw new RuntimeException("必须指定返回码: success,fail之一");
        }

        String dmsg = null;

        if (null != detalMsg && detalMsg.length >= 1) {
            if (detalMsg[0] != null) {
                dmsg = detalMsg[0].toString();
            }
        }

        final String finalDmsg = dmsg;
        @SuppressWarnings("unused")
        String res = JSON.toJSONString(new Object() {
            public int getCode() {
                return code;
            }

            public String getMsg() {
                return msg;
            }

            public Object getData() {
                return data;
            }

            public String getDetalMsg() {
                return finalDmsg;
            }
        });
        renderJson(res);
    }

    protected void checkedAndCreateTable(final String tableName, final String f) {
        if (!Tool.hasTable(tableName)) {
            // 不存在表.新建表
            Db.execute(new ICallback() {
                @Override
                public Integer call(Connection conn) throws SQLException {
                    Statement statement = conn.createStatement();
                    int execute1 = statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + f + ";");
                    statement.close();
                    conn.close();
                    return execute1;
                }
            });
        }
    }
}
