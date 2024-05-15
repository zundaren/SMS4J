package org.dromara.sms4j.core.datainterface;

import org.dromara.sms4j.provider.config.BaseConfig;

import java.util.List;

/**
 * SmsBlendsBeanConfig
 * <p> 读取配置接口，实现该接口中的方法则可以按照自己的形式进行配置的读取
 * <p> 这样只关注最终的配置数据而不关注配置的来源，用户可以自由的选择数据来源的方式</p>
 * <p> 该种方式读取配置在启动阶段完成，无需再有其他操作</p>
 * @author :Wind
 * 2023/8/1  12:06
 **/
public interface SmsBlendsBeanConfig {

    /**
     *  getSupplierConfigList
     * <p> 获取多个厂商的配置，会同时加载进框架中
     * @author :Wind
    */
    List<BaseConfig> getSupplierConfigList();

}
