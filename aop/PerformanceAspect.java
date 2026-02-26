package org.homanhquan.productservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceAspect {
    // Áp dụng cho service hoặc repository
    @Around("execution(* org.homanhquan.productservice.service..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();

        long duration = System.currentTimeMillis() - start;
        log.info("⏱ [Performance] {}.{}() executed in {} ms",
                pjp.getSignature().getDeclaringTypeName(),
                pjp.getSignature().getName(),
                duration);
        return result;
    }
}
