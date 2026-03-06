package com.yubzhou.statemachine.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * AOP aspect that logs the execution time of every public service method.
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(public * com.yubzhou.statemachine..service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String method = joinPoint.getSignature().toShortString();
        try {
            Object result = joinPoint.proceed();
            log.debug("[{}] completed in {} ms", method, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable t) {
            log.debug("[{}] failed after {} ms: {}", method, System.currentTimeMillis() - start, t.getMessage());
            throw t;
        }
    }
}
