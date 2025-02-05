package ru.t1.java.service1.service;

public interface Handler<T> {
    boolean handle(T entity);
}
