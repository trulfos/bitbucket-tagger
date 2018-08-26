package com.github.trulfos.bambooplugins.git.ssh;

public class UnsupportedProtocolException extends RuntimeException {
    UnsupportedProtocolException(String protocol) {
        super("The protocol " + protocol + " is currently not supported.");
    }
}
