package com.microanswer.api.config;

import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.Sqlite3Dialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.template.Engine;
import com.microanswer.api.bean.model._MappingKit;
import com.microanswer.api.controler.PhoneMp3Controler;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

/**
 * JFinal初始化的时候配置类
 * Created by Microanswer on 2017/6/13.
 */
public class Config extends JFinalConfig {

//    private static final String DISK = "D";
    private static final String DISK = "F";

    @Override
    public void configConstant(Constants me) {
        me.setDevMode(true);
        me.setBaseUploadPath(DISK + ":/microanswer/upload/");
    }

    @Override
    public void configRoute(Routes me) {
        me.add("phonemp3", PhoneMp3Controler.class);
    }

    @Override
    public void configEngine(Engine me) {

    }

    @Override
    public void configPlugin(Plugins me) {
        // 配置C3p0数据库连接池插件
        C3p0Plugin c3p0Plugin = new C3p0Plugin("jdbc:sqlite://"+DISK+":/microanswer/db/data.db", "", "");
        c3p0Plugin.setDriverClass("org.sqlite.JDBC"); //指定驱动程序
        me.add(c3p0Plugin);
        // 配置ActiveRecord插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
        me.add(arp);
        arp.setDialect(new Sqlite3Dialect());  //指定 Dialect
        _MappingKit.mapping(arp);
    }

    @Override
    public void configInterceptor(Interceptors me) {

    }

    @Override
    public void configHandler(Handlers me) {

    }


    public static void main(String[] args) {
        JFinal.start("src/main/webapp", 7014, "/");
    }
}
