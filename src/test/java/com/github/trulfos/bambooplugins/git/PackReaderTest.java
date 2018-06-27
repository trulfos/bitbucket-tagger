package com.github.trulfos.bambooplugins.git;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class PackReaderTest {
    @Test
    public void isSatisfiedWithEmtpyPack() throws IOException {
        PackReader reader = getReader("0000");
        assertEquals(0, reader.readPktLines().length);
    }

    @Test
    public void readsLineWhenPresent() throws IOException {
        PktLine[] lines = getReader("0006a\n0005b0005c0000").readPktLines();
        assertEquals(3, lines.length);
        assertEquals("a\n", lines[0].getContent());
        assertEquals("b", lines[1].getContent());
        assertEquals("c", lines[2].getContent());
    }

    private PackReader getReader(String input) {
        byte[] emptyPack = input.getBytes(StandardCharsets.UTF_8);
        return new PackReader(new ByteArrayInputStream(emptyPack));
    }
}