package com.github.trulfos.bambooplugins.git;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class PackWriterTest {

    @Test
    public void writesFlush() {
        assertOutput(
                new PktLine[0],
                null,
                "0000"
        );
    }

    @Test
    public void writesLinesBeforeFlush() {
        PktLine pktLine = new PktLine("Testing");
        assertOutput(
                new PktLine[] { pktLine },
                null,
                "000bTesting0000"
        );
    }

    @Test
    public void writesExtraParmsAfterFirstLine() {
        assertOutput(
                new PktLine[] {
                        new PktLine("a"),
                        new PktLine("b")
                },
                new String[] {"report"},
                "000ca\u0000report0005b0000"
        );
    }

    private void assertOutput(PktLine[] lines, String[] extraParams, String output) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            new PackWriter(stream, extraParams).write(lines);
        } catch (IOException e) {
            throw new RuntimeException("Ups!", e);
        }

        assertArrayEquals(
                output.getBytes(StandardCharsets.UTF_8),
                stream.toByteArray()
        );
    }
}