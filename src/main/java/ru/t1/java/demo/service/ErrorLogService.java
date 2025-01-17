package ru.t1.java.demo.service;

public interface ErrorLogService {

    void saveErrorInfo(Exception e, String methodSignature);

    void sendError(Exception e, String methodSignature);
}