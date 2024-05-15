package org.dromara.sms4j.test;

import org.dromara.sms4j.javase.config.SEInitializer;
import org.dromara.sms4j.test.validate.BlackListValidate;

import sms4j.dao.MySmsDao;
import sms4j.interceptor.MyIntercepterStrategy;
import sms4j.interceptor.MyInterceptor;
import sms4j.local.a.AFactory;

/*
* 测试情况
* */
public class SEYmlBlackListValidateTest {

    public static void main(String[] args) {
        //自定义渠道工厂
        AFactory myFactory = new AFactory();

        //自定义拦截器
        MyInterceptor myInterceptor = new MyInterceptor();

        //自定义拦截器策略
        MyIntercepterStrategy myIntercepterStrategy = new MyIntercepterStrategy();

        //自定义Dao
        MySmsDao mySmsDao =  MySmsDao.getInstance();

        //初始化SMS
        SEInitializer
            //这里之后的方法顺序可以任意排列，但是必须以initializer开始
            .initializer()
            //注册自定义拦截器策略
            .registerIInterceptorStrategy(myIntercepterStrategy)
            //注册自定义拦截器
            .registerSmsMethodInterceptor(myInterceptor)
            //注册自定义渠道工厂
            .registerFactory(myFactory)
            //注册自定义 smsDao
            .registerSmsDao(mySmsDao)
            //这里之前的方法顺序可以任意排列，但是必须以fromYaml结尾
            .fromYaml();

        BlackListValidate blackListValidate = new BlackListValidate("a","11111111111");
        blackListValidate.test();
    }
}
