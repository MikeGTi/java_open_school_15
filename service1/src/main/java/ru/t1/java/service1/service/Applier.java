package ru.t1.java.service1.service;

public interface Applier<T> {
    boolean apply(T entity);
}
