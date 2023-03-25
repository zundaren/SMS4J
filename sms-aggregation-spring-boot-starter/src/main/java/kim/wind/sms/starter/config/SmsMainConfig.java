package kim.wind.sms.starter.config;

import kim.wind.sms.aliyun.service.AlibabaSmsImpl;
import kim.wind.sms.api.SmsBlend;
import kim.wind.sms.comm.delayedTime.DelayedTime;
import kim.wind.sms.comm.utils.RedisUtils;
import kim.wind.sms.comm.utils.SpringUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@ConfigurationProperties(prefix = "sms")     //指定配置文件注入属性前缀
@EnableAsync
@Data
public class SmsMainConfig {

    /** 短信服务商*/
    @Value("${sms.supplier}")
    private String supplier;

    /** 是否开启短信限制*/
    private String restricted;

    /** 是否使用redis进行缓存*/
    private String redisCache = "false";

    /** 单账号每日最大发送量*/
    private Integer accountMax;

    /** 单账号每分钟最大发送*/
    private Integer minuteMax;

    /**核心线程池大小*/
    private Integer corePoolSize = 10;

    /** 最大线程数*/
    private Integer maxPoolSize = 30;

    /** 队列容量*/
    private Integer queueCapacity = 50;

    /** 活跃时间*/
    private Integer keepAliveSeconds = 60;

    /** 线程名字前缀*/
    private String threadNamePrefix = "sms-executor-";

    /** 设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean*/
    private Boolean shutdownStrategy = true;

    @Bean
    public SpringUtil springUtil(){
        return new SpringUtil();
    }

    @Bean
    public SmsBlend smsBlend(){
        SmsBlend smsBlend = null;
        switch (supplier){
            case "alibaba":
                smsBlend = new AlibabaSmsImpl();
        }
        return smsBlend;
    }

    @Bean("smsExecutor")
    protected Executor taskExecutor(){
        // 创建一个线程池对象
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 线程池对拒绝任务的处理策略,当线程池没有处理能力的时候，该策略会直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //初始化线程池
        executor.initialize();
        return executor;
    }

    @Bean
    @ConditionalOnProperty(prefix = "sms", name = "restricted", havingValue = "true")
    public AopAdvice aopAdvice(){
        return new AopAdvice();
    }

    /** 如果启用了redis作为缓存则注入redis工具类*/
    @Bean
    @ConditionalOnProperty(prefix = "sms", name = "redisCache", havingValue = "true")
    public RedisUtils redisUtils(RedisTemplate<String, Object> redisTemplate){
        return new RedisUtils(redisTemplate);
    }

    @Bean
    public DelayedTime delayedTime(){
        return new DelayedTime();
    }
}