package com.yubzhou.statemachine.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 状态回滚配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "rollback")
public class RollbackProperties {

    /** 最大历史回滚条数 */
    private int maxHistoryCount = 5;
}
