package org.dromara.sms4j.example;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.comm.constant.SupplierConstant;
import org.dromara.sms4j.comm.exception.SmsBlendException;
import org.dromara.sms4j.comm.utils.SmsUtils;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * @author sh1yu
 */
@Slf4j
@SpringBootTest
public class SmsMethodInterceptorTest {
    /**
     * 填测试手机号
     */
    private static final String PHONE = "11111111111";
    private static final String PHONE1 = "22222222222";

    //基础发送测试，即黑名单、账户限制、渠道限制都不预设直接发送（新增参数全为空）
    @Test
    public void test1() {
        System.out.println("------------");
        SmsBlend smsBlend = SmsFactory.getBySupplier(SupplierConstant.LOCAL);
        SmsResponse smsResponse = smsBlend.sendMessage(PHONE, SmsUtils.getRandomInt(6));
        Assert.isTrue(smsResponse.isSuccess());
        System.out.println(smsResponse.getData());


    }

    //第二个账号的测试
    @Test
    public void test2() {
        System.out.println("------------");

        SmsBlend smsBlend = SmsFactory.getBySupplier(SupplierConstant.LOCAL);
        SmsResponse smsResponse = smsBlend.sendMessage(PHONE1, SmsUtils.getRandomInt(6));
        Assert.isTrue(smsResponse.isSuccess());
        System.out.println(smsResponse.getData());

    }

    //参数校验测试
    @Test
    public void test3() {
        System.out.println("------------");

        SmsBlend sb = SmsFactory.getBySupplier(SupplierConstant.LOCAL);
        Assertions.assertThrows(SmsBlendException.class,
            () -> sb.sendMessage(PHONE, new LinkedHashMap<>())
        );
        Assertions.assertThrows(SmsBlendException.class,
            () -> sb.sendMessage("", SmsUtils.getRandomInt(6))
        );
        Assertions.assertThrows(SmsBlendException.class,
            () -> sb.sendMessage(PHONE, "")
        );
        Assertions.assertThrows(SmsBlendException.class,
            () -> sb.sendMessage(PHONE, "111", new LinkedHashMap<>())
        );
        Assertions.assertThrows(SmsBlendException.class,
            () -> sb.massTexting(Collections.singletonList(PHONE), "")
        );
        Assertions.assertThrows(SmsBlendException.class,
            () -> sb.massTexting(Collections.singletonList(PHONE), "2222", new LinkedHashMap<>())
        );
        Assertions.assertThrows(SmsBlendException.class,
            () -> sb.massTexting(new ArrayList<>(), "321321")
        );
    }

    //黑名单测试
    @Test
    public void test4() {
        System.out.println("------------");

        SmsBlend smsBlend = SmsFactory.getBySupplier(SupplierConstant.LOCAL);
        //单黑名单添加
        smsBlend.joinInBlacklist(PHONE);
        SmsBlendException knowEx = null;
        try {
            smsBlend.sendMessage(PHONE, SmsUtils.getRandomInt(6));
        } catch (SmsBlendException e) {
            knowEx = e;
            System.out.println(knowEx.getMessage());
        }
        Assert.notNull(knowEx);
        //单黑名单移除
        smsBlend.removeFromBlacklist(PHONE);
        SmsResponse smsResponse = smsBlend.sendMessage(PHONE, SmsUtils.getRandomInt(6));
        Assert.isTrue(smsResponse.isSuccess());
        //批量黑名单添加
        smsBlend.batchJoinBlacklist(Collections.singletonList(PHONE));
        knowEx = null;
        try {
            smsBlend.sendMessage(PHONE, SmsUtils.getRandomInt(6));
        } catch (SmsBlendException e) {
            knowEx = e;
            System.out.println(knowEx.getMessage());
        }
        Assert.notNull(knowEx);
        //批量黑名单添加
        smsBlend.batchRemovalFromBlacklist(Collections.singletonList(PHONE));
        smsResponse = smsBlend.sendMessage(PHONE, SmsUtils.getRandomInt(6));
        Assert.isTrue(smsResponse.isSuccess());

    }

    //账号级上限测试、需成功发送4笔，再发就会报错 参数配置 4
    @Test
    public void test5() {
        System.out.println("------------");
        SmsBlend sb = SmsFactory.getBySupplier(SupplierConstant.LOCAL);
        SmsResponse smsResponse = sb.sendMessage(PHONE, SmsUtils.getRandomInt(6));
        Assert.isTrue(smsResponse.isSuccess());
        Assertions.assertThrows(SmsBlendException.class, () -> sb.sendMessage(PHONE, SmsUtils.getRandomInt(6)));
    }

    //渠道级上限测试、需成功发送6笔，再发就会报错 参数配置 6
    @Test
    public void test6() {
        System.out.println("------------");

        SmsResponse smsResponse = SmsFactory.getBySupplier(SupplierConstant.LOCAL)
            .sendMessage(PHONE1, SmsUtils.getRandomInt(6));
        Assert.isTrue(smsResponse.isSuccess());

        SmsBlendException knowEx = null;
        try {
            SmsFactory.getBySupplier(SupplierConstant.LOCAL).sendMessage(PHONE1, SmsUtils.getRandomInt(6));
        } catch (SmsBlendException e) {
            knowEx = e;
            System.out.println(knowEx.getMessage());
        }
        Assert.notNull(knowEx);
    }

}