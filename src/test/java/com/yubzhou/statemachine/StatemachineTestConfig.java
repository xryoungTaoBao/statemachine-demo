package com.yubzhou.statemachine;

import com.yubzhou.statemachine.config.OrderStateMachineConfig;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.statemachine.boot.autoconfigure.StateMachineRedisRepositoriesAutoConfiguration;

/**
 * 测试专用配置 - 排除需要外部服务的自动配置
 */
@Configuration
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    RedisAutoConfiguration.class,
    RedisReactiveAutoConfiguration.class,
    RedissonAutoConfigurationV2.class,
    StateMachineRedisRepositoriesAutoConfiguration.class
})
@Import(OrderStateMachineConfig.class)
public class StatemachineTestConfig {
}
