package com.yubzhou.statemachine.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the distributed lock.
 */
@Data
@Component
@ConfigurationProperties(prefix = "distributed-lock")
public class DistributedLockProperties {

    /** Max seconds to wait before giving up acquiring the lock. */
    private long waitTime = 5;

    /** Seconds to hold the lock before auto-release. */
    private long leaseTime = 30;

    /** Redis key prefix for all distributed locks. */
    private String keyPrefix = "sm:lock:";
}
