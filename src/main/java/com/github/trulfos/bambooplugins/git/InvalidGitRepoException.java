package com.github.trulfos.bambooplugins.git;

public class InvalidGitRepoException extends RuntimeException {
    InvalidGitRepoException(String message) {
        super(message);
    }
}
