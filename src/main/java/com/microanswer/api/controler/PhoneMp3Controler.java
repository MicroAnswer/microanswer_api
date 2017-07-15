package com.microanswer.api.controler;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.upload.UploadFile;
import com.microanswer.api.bean.model.Phonemp3app;

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


    // 获取最新版app信息
    public void getNewApp() {
        // 查询一条数据,按id排序,则找到了最新的一条数据
        Phonemp3app first = Phonemp3app.dao.findFirst("select id, version, link from phonemp3app order by id desc limit 0,1");
        answer(SUCCESS, "", first);
    }

    // 获取app封面图
    public void getCover() throws IOException {
        checkedAndCreateTable("cover", "(id INTEGER PRIMARY KEY AUTOINCREMENT, title CHAR, picurl CHAR, createdat CHAR, updateat CHAR)");

        List<Object> records = Db.query("SELECT picurl from cover order by id desc limit 0,1");
        if (records != null && records.size() == 1) {
            String path = records.get(0).toString();
            File f = new File(path);
            if (f.exists() && f.isFile()) {
//                getResponse().setContentType("image/*");
//                getResponse().setHeader("Content-Length", f.length() + "");
//                ServletOutputStream outputStream = getResponse().getOutputStream();
//                FileInputStream fileInputStream = new FileInputStream(f);
//                byte[] data = new byte[1024];
//                int dataSize = 0;
//                while ((fileInputStream.read(data)) != -1) {
//                    outputStream.write(data, 0, dataSize);
//                }
//                outputStream.flush();
//                renderNull();
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
}
