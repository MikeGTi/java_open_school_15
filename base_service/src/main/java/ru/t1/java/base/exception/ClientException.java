package ru.t1.java.base.exception;

public class ClientException extends RuntimeException {
    public ClientException() {
    }

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
