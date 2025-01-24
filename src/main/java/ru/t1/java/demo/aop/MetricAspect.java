package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.MetricDto;
import ru.t1.java.demo.service.MetricService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * Аспект для сбора и логирования метрик выполнения методов, помеченных аннотацией {@link Metric}.
 * Если время выполнения метода превышает заданный порог, метрика отправляется в Kafka.
 *
 * @author mboychook
 * @version 1.0
 * @since 7.11.2024
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MetricAspect {

    /**
     * Сервис отправки MetricDto сообщений.
     */
    private final MetricService<MetricDto> metricService;

    /**
     * Порог времени выполнения метода, после которого метрика отправляется в Kafka.
     */
    @Value("track.method-execution-time-limit-ms")
    private static long executionTimeThreshold;

    /**
     * Advice, который выполняется вокруг методов, помеченных аннотацией {@link Metric}.
     * Если время выполнения метода превышает заданный порог, метрика отправляется в Kafka.
     *
     * @param pJoinPoint Точка соединения, представляющая выполнение метода.
     * @return Результат выполнения метода.
     * @throws Throwable Исключение, которое может быть выброшено методом.
     */
    @Around("@annotation(ru.t1.java.demo.aop.Metric)")
    public CompletableFuture<Object> trackMethodExecutionTime(ProceedingJoinPoint pJoinPoint) throws Throwable {

        log.info("Call method: {}", pJoinPoint.getSignature().toShortString());
        long beforeTime = System.currentTimeMillis();
        Object result;

        try {
            result = pJoinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - beforeTime;

            log.info("Execution time: {} ms, and limit is {} ms", executionTime, executionTimeThreshold);
            if (executionTime > executionTimeThreshold) {
                metricService.sendMetric(getMetricDto(pJoinPoint, executionTime));
            }
        }
        return CompletableFuture.completedFuture(result);
    }

    private MetricDto getMetricDto(ProceedingJoinPoint pJoinPoint, Long executionTime) {
        return MetricDto.builder()
                        .methodName(pJoinPoint.getSignature().getName())
                        .methodArgs(Arrays.toString(pJoinPoint.getArgs()))
                        .executionTime(executionTime)
                        .created(LocalDateTime.now())
                        .build();
    }
}
