package com.github.trulfos.bambooplugins.git.ssh;

public class RefNotUpdatedException extends RuntimeException {
    RefNotUpdatedException(String refError) {
        super("Ref not updated. Error from the server was: " + refError);
    }
}
