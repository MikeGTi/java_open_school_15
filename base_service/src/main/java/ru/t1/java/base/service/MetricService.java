package ru.t1.java.base.service;


public interface MetricService<T> {
    void sendMetric(T object);
}