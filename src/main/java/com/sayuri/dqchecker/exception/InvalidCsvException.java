package com.sayuri.dqchecker.exception;

public class InvalidCsvException extends InvalidFileException {

    public InvalidCsvException(String message) {
        super(message);
    }

    public InvalidCsvException(String message, Throwable cause) {
        super(message, cause);
    }
}
