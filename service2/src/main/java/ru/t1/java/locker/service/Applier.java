package ru.t1.java.locker.service;

public interface Applier<T> {
    boolean apply(T entity);
}
