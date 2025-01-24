package ru.t1.java.demo.service;


public interface MetricService<T> {
    void sendMetric(T object);
}