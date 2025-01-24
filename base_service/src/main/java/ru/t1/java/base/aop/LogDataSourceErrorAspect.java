package ru.t1.java.base.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.base.service.ErrorLogService;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(0)
public class LogDataSourceErrorAspect {

    private final ErrorLogService errorLogService;

    @AfterThrowing(
            pointcut = "@annotation(ru.t1.java.base.aop.LogDataSourceError)",
            throwing = "e")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logError(JoinPoint joinPoint, Exception e) {
        log.error("An error occurred while processing {}", joinPoint.getSignature().getName());
        try {
            errorLogService.sendError(e, joinPoint.getSignature().toShortString());
        } catch (Exception exc) {
            log.error("An error occurred while trying to send event to kafka {}", joinPoint.getSignature().getName());
            errorLogService.saveError(exc, joinPoint.getSignature().toShortString());
        }
    }
}