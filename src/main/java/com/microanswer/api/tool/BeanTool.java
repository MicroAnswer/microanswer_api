package com.microanswer.api.tool;

import com.jfinal.config.Constants;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.activerecord.dialect.Sqlite3Dialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.activerecord.generator.TypeMapping;
import com.jfinal.plugin.druid.DruidPlugin;
import com.microanswer.api.config.Config;

import javax.sql.DataSource;

/**
 * Created by Microanswer on 2017/4/17.
 */
public class BeanTool {
    public static void main(String[] args) {
        Config c = new Config();
        c.configConstant(new Constants());

        // base model 所使用的包名
        String baseModelPkg = "com.microanswer.api.bean.base";
        // base model 文件保存路径
        String baseModelDir = PathKit.getWebRootPath() + "/src/main/java/com/microanswer/api/bean/base";

        // model 所使用的包名
        String modelPkg = "com.microanswer.api.bean.model";
        // model 文件保存路径
        String modelDir = baseModelDir + "/../model";

        DruidPlugin dp = new DruidPlugin("jdbc:sqlite://F:/microanswer/db/data.db", "", "");

        dp.start();

        DataSource dataSource = dp.getDataSource();

        // SqliteGenerator sqliteGenerator = new SqliteGenerator();

        Generator gernerator = new Generator(dataSource, baseModelPkg, baseModelDir, modelPkg, modelDir);
        gernerator.setDialect(new Sqlite3Dialect());
        gernerator.setTypeMapping(new TypeMapping() {
            @Override
            public String getType(String typeString) {
                String type = super.getType(typeString);
                if (type == null || "".equals(type)) {
                    return typeString;
                } else {
                    return type;
                }
            }
        });
        gernerator.addExcludedTable("sqlite_sequence");
        gernerator.setGenerateChainSetter(true);
        gernerator.setGenerateDaoInModel(true);
        gernerator.setGenerateDataDictionary(true);

        gernerator.generate();

    }
}
