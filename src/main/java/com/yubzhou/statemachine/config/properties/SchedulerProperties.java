package com.yubzhou.statemachine.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 定时任务配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "scheduler")
public class SchedulerProperties {

    /** 支付超时任务配置 */
    private TaskConfig paymentTimeout = new TaskConfig();

    /** 自动收货任务配置 */
    private TaskConfig autoReceive = new TaskConfig();

    /** 事件日志清理任务配置 */
    private CleanupTaskConfig eventLogCleanup = new CleanupTaskConfig();

    /**
     * 通用定时任务配置
     */
    @Data
    public static class TaskConfig {
        /** 是否启用 */
        private boolean enabled = true;
        /** Cron 表达式 */
        private String cron = "0 * * * * ?";
    }

    /**
     * 清理任务配置
     */
    @Data
    public static class CleanupTaskConfig extends TaskConfig {
        /** 保留天数 */
        private int retentionDays = 90;
    }
}
