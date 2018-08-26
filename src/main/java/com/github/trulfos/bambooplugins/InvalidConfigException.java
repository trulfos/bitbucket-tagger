package com.github.trulfos.bambooplugins;

public class InvalidConfigException extends RuntimeException {
    InvalidConfigException(String message, RuntimeException exception) {
        super(message,exception);
    }

    InvalidConfigException(String message) {
        super(message);
    }
}
