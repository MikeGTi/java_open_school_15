package ru.t1.java.service1.service;


import java.util.function.Predicate;

public abstract class Rule<T> implements Applier<T>, Handler<T> {

    private final Predicate<T> predicate;

    public Rule(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean apply(T entity) throws ClassCastException {
        return predicate.test(entity);
    }
}
