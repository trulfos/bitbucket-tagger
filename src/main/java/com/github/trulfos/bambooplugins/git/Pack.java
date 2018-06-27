package com.github.trulfos.bambooplugins.git;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Represents a pack.
 *
 * Currently only supports empty packs.
 */
public class Pack {
    void writeTo(OutputStream stream) throws IOException {
        try {
            byte[] pack = {'P', 'A', 'C', 'K', 0, 0, 0, 2, 0, 0, 0, 0};
            byte[] sha1 = MessageDigest.getInstance("SHA-1").digest(pack);
            stream.write(pack);
            stream.write(sha1);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 not supported :/");
        }
    }
}
