package com.github.trulfos.bambooplugins.git;

public class InvalidHashException extends RuntimeException {
    InvalidHashException(String hash) {
        super("Invalid hash given to ref: " + hash);
    }
}
