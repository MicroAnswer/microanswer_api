package com.microanswer.api.intercept;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.LogKit;
import com.microanswer.api.controler.BaseControler;

/**
 * //全局异常捕获
 * Created by Microanswer on 2017/7/6.
 */
public class ExceptionIntercept implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        Exception mme = null;
        String actionKey = inv.getActionKey();
        LogKit.info("访问:" + actionKey);
        try {
            inv.invoke();
        } catch (Exception e) {
            LogKit.error(inv.getActionKey(), e);
            mme = e;
        } finally {
            if (null != mme) {
                // 有错误,输出错误
                ((BaseControler) inv.getController()).answer(BaseControler.FAIL, "服务器出现异常", null, mme.getMessage());
            }
        }
    }
}
