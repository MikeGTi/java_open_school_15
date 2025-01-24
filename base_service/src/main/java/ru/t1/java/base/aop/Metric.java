package ru.t1.java.base.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для метрик.
 * Применяется к методам для сбора метрик о времени выполнения метода.
 *
 * @author mboychook
 * @version 1.0
 * @since 24.01.2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
public @interface Metric {
    /**
     * Значение метрики.
     * По умолчанию пустая строка.
     *
     * @return Значение метрики.
     */
    String value() default "";
}