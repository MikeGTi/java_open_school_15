package ru.t1.java.base.service;

public interface ErrorLogService {

    void saveError(Exception e, String methodSignature);

    void sendError(Exception e, String methodSignature);
}