package com.github.trulfos.bambooplugins.git;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PktLine {
    private final byte[] content;

    public PktLine(String content) {
        this.content = content.getBytes(StandardCharsets.UTF_8);

        if (content.length() > 65516) {
            throw new IllegalArgumentException("PktLine content exceeds 65516 bytes");
        }

        if (content.length() == 0) {
            throw new IllegalArgumentException("PktLine content cannot be empty");
        }
    }

    public PktLine() {
        this.content = null;
    }

    PktLine(DataInputStream stream) throws IOException {
        byte[] header = new byte[4];
        stream.readFully(header);

        int length;
        try {
            length = Integer.parseInt(
                    new String(header, StandardCharsets.UTF_8),
                    16
            );
        } catch (NumberFormatException e) {
            throw new IOException("Invalid pkt-line length format received");
        }

        if (length == 0) {
            content = null;
            return;
        }

        if (length < 4) {
            throw new IOException("Pkt-line length out of range " + length);
        }

        content = new byte[length - 4];
        stream.readFully(content);
    }

    public void writeTo(OutputStream stream) throws IOException {
        stream.write(
            content == null
                ? getPrefix(0)
                : getPrefix(4 + content.length)
        );

        if (content != null) {
           stream.write(content);
        }
    }

    private byte[] getPrefix(int length) {
        return String.format("%04x", length)
                .getBytes(StandardCharsets.UTF_8);
    }

    public String getContent() {
        return new String(content, StandardCharsets.UTF_8);
    }

    public boolean isFlush() {
        return content == null;
    }
}
