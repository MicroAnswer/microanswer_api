package com.microanswer.api.controler;

import com.alibaba.druid.util.StringUtils;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.microanswer.api.bean.model.Phonemp3app;
import com.microanswer.api.tool.Tool;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * 提供给PhoneMp3应用使用的接口
 * Created by Microanswer on 2017/6/13.
 */
public class PhoneMp3Controler extends BaseControler {

    // 下载apk
    public void downloadApk() {
        checkedAndCreateTable("phonemp3app", "(id INTEGER PRIMARY KEY AUTOINCREMENT, name CHAR, version CHAR, link CHAR, newfunction CHAR, createdat CHAR, updateat CHAR)");
        String id = getPara("id");
        if (StringUtils.isEmpty(id)) {
            id = "-1";
        }
        Phonemp3app first = Phonemp3app.dao.findFirst("select * from phonemp3app where id=" + id + ";");
        if (first == null || "-".equals(first.getLink())) {
            getResponse().setStatus(404);
            renderNull();
            return;
        }
        renderFile(new File(first.getLink().toString()));
    }

    // 获取最新版app信息
    public void getNewApp() {
        checkedAndCreateTable("phonemp3app", "(id INTEGER PRIMARY KEY AUTOINCREMENT, name CHAR, version CHAR, link CHAR, newfunction CHAR, createdat CHAR, updateat CHAR)");

        // 查询一条数据,按id排序,则找到了最新的一条数据
        Phonemp3app first = Phonemp3app.dao.findFirst("select * from phonemp3app order by id desc limit 0,1");
        first.setLink("http://182.150.20.97:33999/phonemp3/downloadApk?id=" + first.getId());
        answer(SUCCESS, "获取成功", first);
    }

    // 获取app封面图
    public void getCover() throws IOException {
        checkedAndCreateTable("cover", "(id INTEGER PRIMARY KEY AUTOINCREMENT, title CHAR, picurl CHAR, createdat CHAR, updateat CHAR)");
        List<Object> records = Db.query("SELECT picurl from cover order by id desc limit 0,1");
        if (records != null && records.size() == 1) {
            String path = records.get(0).toString();
            File f = new File(path);
            if (f.exists() && f.isFile()) {
                renderFile(f);
                return;
            }
        }
        getResponse().setStatus(404);
        renderNull();
    }

    // 上传封面
    public void uploadCover() {
        checkedAndCreateTable("cover", "(id INTEGER PRIMARY KEY AUTOINCREMENT, title CHAR, picurl CHAR, createdat CHAR, updateat CHAR)");

        UploadFile file = getFile();
        File file1 = file.getFile();
        File parentFile = new File(file1.getParentFile().getParentFile(), "cover");
        File newFile = new File(parentFile, file1.getName());
        if (newFile.exists()) {
            answer(FAIL, "上传失败", null, "已存在该文件.");
            return;
        }
        if (!parentFile.exists()) {
            if (parentFile.mkdirs()) {
                if (file1.renameTo(newFile)) {
                    String s = System.currentTimeMillis() + "";
                    Db.update("insert into cover (title,picurl,createdat,updateat) " +
                            "values ('" + newFile.getName() +
                            "','" + newFile.getAbsolutePath() +
                            "','" + s + "','" + s + "')");
                    answer(SUCCESS, "上传成功", null);
                } else {
                    answer(FAIL, "上传失败", null, "移动文件到cover目录失败.");
                }
            } else {
                answer(FAIL, "上传失败", null, "创建cover目录失败");
            }
        } else {
            if (file1.renameTo(newFile)) {
                String s = System.currentTimeMillis() + "";
                int a = 0;
                Db.update("insert into cover (title,picurl,createdat,updateat) " +
                        "values ('" + newFile.getName() +
                        "','" + newFile.getAbsolutePath() +
                        "','" + s + "','" + s + "')");
                answer(SUCCESS, "上传成功", null);
            } else {
                answer(FAIL, "上传失败", null, "移动文件到cover目录失败.");
            }
        }
    }

    // 上传新版app
    public void uploadNewApp() {
        checkedAndCreateTable("phonemp3app", "(id INTEGER PRIMARY KEY AUTOINCREMENT, name CHAR, version CHAR, link CHAR, newfunction CHAR, createdat CHAR, updateat CHAR)");

        // jfinal 上传的临时文件
        File uploadfile = getFile().getFile();

        String name = getPara("name");
        if (StringUtils.isEmpty(name)) {
            answer(FAIL, "上传失败", null, "没有指定name字段的值");
            return;
        }

        String newfunction = getPara("newfunction");
        if (StringUtils.isEmpty(newfunction)) {
            answer(FAIL, "上传失败", null, "没有指定newfunction(新版特性)");
            return;
        }

        // 取得文件名
        String fileName = uploadfile.getName();

        // 解析文件名获取版本(phonemp3-1.0.0-release.apk)
        String[] _split = fileName.split("-");

        if (_split.length != 3) {
            answer(FAIL, "文件不合法", null);
            uploadfile.delete();
            return;
        }

        String version = _split[1];


        // upload目录
        File parentFile = uploadfile.getParentFile();

        // microanswer目录
        String microanswerDir = parentFile.getParent();

        // apk目录
        File apkDir = new File(microanswerDir, "apk");

        // 判断是否存在
        if (!apkDir.exists()) {
            if (!apkDir.mkdirs()) {
                answer(FAIL, "上传失败", null, "apk目录创建失败");
                return;
            }
        }

        // 创建移动临时文件的目标文件
        File newVersionApkFile = new File(apkDir, fileName);

        // 移动临时文件
        if (uploadfile.renameTo(newVersionApkFile)) {
            // 移动成功
            // 插入数据到数据库
            String link = null;
            try {
                link = newVersionApkFile.getCanonicalPath();
            } catch (Exception e) {
                LogKit.error(e.getMessage());
            }
            Db.update("insert into phonemp3app (name,version,newfunction,link) " +
                    "values ('" + name +
                    "','" + version +
                    "','" + newfunction + "','" + link + "')");
            answer(SUCCESS, "上传成功", null);
        } else {
            // 移动失败
            answer(FAIL, "上传失败", null, "移动文件到apk目录失败");
        }


    }
}
