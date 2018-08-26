package com.github.trulfos.bambooplugins.git;

public class InvalidRefNameException extends RuntimeException {
    InvalidRefNameException(String refName) {
        super("Invalid ref name " + refName);
    }
}
