package ru.t1.java.locker.service;

public interface Handler<T> {
    boolean handle(T entity);
}
