package org.dromara.sms4j.source;

import org.dromara.sms4j.core.datainterface.SmsReadConfig;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.dromara.sms4j.provider.config.BaseConfig;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sms4j.local.a.AConfig;
import sms4j.local.b.BConfig;
import sms4j.local.b.BFactory;
import sms4j.local.c.CConfig;
import sms4j.local.c.CFactory;

import java.util.ArrayList;
import java.util.List;

public class ReLoadConfig implements SmsReadConfig {
    @EventListener
    public void init(ContextRefreshedEvent event){
        // 创建SmsBlend 短信实例
        SmsFactory.createSmsBlend(this,"在配置中定义的configId");
    }
    @Override
    public BaseConfig getSupplierConfig(String configId) {
        // 服务商2

        AConfig aConfig1 = new AConfig();
        aConfig1.setMaximum(5);
        aConfig1.setConfigId("b1");
        return aConfig1;
    }

    @Override
    public List<BaseConfig> getSupplierConfigList() {
        List<BaseConfig> configs = new ArrayList<BaseConfig>();

        // 服务商1
        CConfig cConfig = new CConfig();
        cConfig.setConfigId("a1");
        cConfig.setMaximum(5);
        configs.add(cConfig);
        CFactory cFactory = new CFactory();


        BFactory bFactory = new BFactory();

        // 服务商3
        AConfig aConfig = new AConfig();
        aConfig.setMaximum(5);
        aConfig.setConfigId("c1");
        configs.add(aConfig);


        BConfig bConfig = new BConfig();
        bConfig.setMaximum(5);
        bConfig.setConfigId("a2");
        configs.add(bConfig);
        return configs;
    }
}