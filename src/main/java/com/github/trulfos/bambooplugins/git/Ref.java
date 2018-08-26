package com.github.trulfos.bambooplugins.git;

public class Ref {
    private String name;
    private String hash;

    public Ref(String name, String hash) {
        if (hash == null || !hash.matches("^[0-9a-f]{40}$")) {
            throw new InvalidHashException(hash);
        }

        if (name == null || !name.matches("^[^/].*/.*")) { //TODO: Could use some improvement
            throw new InvalidRefNameException(name);
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

    public boolean equals(Object other) {
        if (!(other instanceof Ref)) {
            return false;
        }

        Ref otherRef = (Ref) other;

        return name.equals(otherRef.name) &&
                hash.equals(otherRef.hash);
    }
}
