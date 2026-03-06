package com.yubzhou.statemachine.common.aspect;

import com.yubzhou.statemachine.common.annotation.DistributedLock;
import com.yubzhou.statemachine.common.exception.BusinessException;
import com.yubzhou.statemachine.config.properties.DistributedLockProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * AOP aspect that enforces distributed locking for methods annotated with
 * {@link DistributedLock}.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;
    private final DistributedLockProperties lockProperties;

    @Around("@annotation(lock)")
    public Object around(ProceedingJoinPoint pjp, DistributedLock lock) throws Throwable {
        Object[] args = pjp.getArgs();
        String keyId = args.length > 0 ? String.valueOf(args[0]) : "default";
        String lockKey = lockProperties.getKeyPrefix() + lock.prefix() + keyId;

        long waitTime  = lock.waitTime()  > 0 ? lock.waitTime()  : lockProperties.getWaitTime();
        long leaseTime = lock.leaseTime() > 0 ? lock.leaseTime() : lockProperties.getLeaseTime();

        RLock rLock = redissonClient.getLock(lockKey);
        boolean acquired = rLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        if (!acquired) {
            throw new BusinessException("Could not acquire distributed lock: " + lockKey);
        }

        try {
            return pjp.proceed();
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
}
