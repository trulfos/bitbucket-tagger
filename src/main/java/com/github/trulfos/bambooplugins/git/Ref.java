package com.github.trulfos.bambooplugins.git;

public class Ref {
    private String name;
    private String hash;

    public Ref(String name, String hash) {
        if (!hash.matches("^[0-9a-f]{40}$")) {
            throw new IllegalArgumentException("Invalid hash given to ref: " + hash);
        }

        if (!name.matches("^[^/].*/.*")) { //TODO: Could use some improvement
            throw new IllegalArgumentException("Invalid refname " + name);
        }

        this.name = name;
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }
}
