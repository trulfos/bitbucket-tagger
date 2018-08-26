package com.github.trulfos.bambooplugins.git.ssh;

public class GitProtcolError extends RuntimeException {
    GitProtcolError(String s) {
        super(s);
    }

    GitProtcolError(String s, Exception e) {
        super(s, e);
    }
}
