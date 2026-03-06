package com.yubzhou.statemachine.common.annotation;

import java.lang.annotation.*;

/**
 * Marks a method as requiring a distributed lock.
 * The lock key is formed from {@link #prefix()} + the first method argument.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /** Lock key prefix. */
    String prefix() default "lock:";

    /** Max seconds to wait when acquiring the lock. */
    long waitTime() default 5;

    /** Seconds to hold the lock before auto-release. */
    long leaseTime() default 30;
}
