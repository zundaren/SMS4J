package org.dromara.sms4j.starter.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.aliyun.config.AlibabaFactory;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.universal.SupplierConfig;
import org.dromara.sms4j.cloopen.config.CloopenFactory;
import org.dromara.sms4j.comm.constant.Constant;
import org.dromara.sms4j.comm.utils.SmsUtil;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.dromara.sms4j.core.proxy.SmsInvocationHandler;
import org.dromara.sms4j.ctyun.config.CtyunFactory;
import org.dromara.sms4j.emay.config.EmayFactory;
import org.dromara.sms4j.huawei.config.HuaweiFactory;
import org.dromara.sms4j.jdcloud.config.JdCloudFactory;
import org.dromara.sms4j.netease.config.NeteaseFactory;
import org.dromara.sms4j.provider.config.BaseConfig;
import org.dromara.sms4j.provider.config.SmsConfig;
import org.dromara.sms4j.provider.factory.BaseProviderFactory;
import org.dromara.sms4j.provider.factory.ProviderFactoryHolder;
import org.dromara.sms4j.starter.aop.SpringRestrictedProcess;
import org.dromara.sms4j.tencent.config.TencentFactory;
import org.dromara.sms4j.unisms.config.UniFactory;
import org.dromara.sms4j.yunpian.config.YunPianFactory;
import org.dromara.sms4j.zhutong.config.ZhutongFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
@AllArgsConstructor
public class SmsBlendsInitializer {

    private List<BaseProviderFactory<? extends SmsBlend, ? extends SupplierConfig>> factoryList;

    private final SmsConfig smsConfig;
    private final Map<String, Map<String, Object>> blends;

    @PostConstruct
    public void initBlends() {
        this.registerDefaultFactory();
        // 注册短信对象工厂
        ProviderFactoryHolder.registerFactory(factoryList);
        // 解析供应商配置
        for(String configId : blends.keySet()) {
            Map<String, Object> configMap = blends.get(configId);
            Object supplierObj = configMap.get(Constant.SUPPLIER_KEY);
            String supplier = supplierObj == null ? "" : String.valueOf(supplierObj);
            supplier = StrUtil.isEmpty(supplier) ? configId : supplier;
            BaseProviderFactory<SmsBlend, SupplierConfig> providerFactory = (BaseProviderFactory<SmsBlend, org.dromara.sms4j.api.universal.SupplierConfig>) ProviderFactoryHolder.requireForSupplier(supplier);
            if(providerFactory == null) {
                log.warn("创建\"{}\"的短信服务失败，未找到供应商为\"{}\"的服务", configId, supplier);
                continue;
            }
            configMap.put("config-id", configId);
            SmsUtil.replaceKeysSeperator(configMap, "-", "_");
            JSONObject configJson = new JSONObject(configMap);
            org.dromara.sms4j.api.universal.SupplierConfig supplierConfig = JSONUtil.toBean(configJson, providerFactory.getConfigClass());
            if(Boolean.TRUE.equals(smsConfig.getRestricted())) {
                SmsFactory.createRestrictedSmsBlend(supplierConfig);
            } else {
                SmsFactory.createSmsBlend(supplierConfig);
            }
        }

        //注册短信拦截实现
        SmsInvocationHandler.setRestrictedProcess(new SpringRestrictedProcess());
    }

    /**
     * 注册默认工厂实例
     */
    private void registerDefaultFactory() {
        ProviderFactoryHolder.registerFactory(AlibabaFactory.instance());
        ProviderFactoryHolder.registerFactory(CloopenFactory.instance());
        ProviderFactoryHolder.registerFactory(CtyunFactory.instance());
        ProviderFactoryHolder.registerFactory(EmayFactory.instance());
        ProviderFactoryHolder.registerFactory(HuaweiFactory.instance());
        ProviderFactoryHolder.registerFactory(JdCloudFactory.instance());
        ProviderFactoryHolder.registerFactory(NeteaseFactory.instance());
        ProviderFactoryHolder.registerFactory(TencentFactory.instance());
        ProviderFactoryHolder.registerFactory(UniFactory.instance());
        ProviderFactoryHolder.registerFactory(YunPianFactory.instance());
        ProviderFactoryHolder.registerFactory(ZhutongFactory.instance());
    }

    /**
     * 将Map中所有key的分隔符转换为新的分隔符
     * @param map map对象
     * @param seperator 旧分隔符
     * @param newSeperator 新分隔符
     */
    public static void replaceKeysSeperator(Map<String, BaseConfig> map, String seperator, String newSeperator) {
        if(CollUtil.isEmpty(map)) {
            return;
        }
        List<String> keySet = new ArrayList<>(map.keySet());
        for(String key : keySet) {
            if(StrUtil.isEmpty(key) || !key.contains(seperator)) {
                continue;
            }
//            String value = String.valueOf(map.get(key));
            String newKey = key.replaceAll(seperator, newSeperator);
            newKey = StrUtil.toCamelCase(newKey);
            map.putIfAbsent(newKey, map.get(key));
            map.remove(key);
        }
    }

}