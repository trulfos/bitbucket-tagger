package com.github.trulfos.bambooplugins.git;

import com.github.trulfos.bambooplugins.git.PktLine;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.*;

public class PktLineTest {
    @Test
    public void generatesFlushWhenConstructedWithNoArgs() throws IOException {
        assertOutput(new PktLine(), "0000");
    }

    @Test
    public void claimsToBeFlushWhenFlush() {
        assertTrue(new PktLine().isFlush());
    }

    @Test
    public void claimsNotToBeFlushWhenNotFlush() {
        assertFalse(new PktLine("bla").isFlush());
    }

    @Test
    public void addsLength() {
        assertOutput(
                new PktLine("meh\n"),
                "0008meh\n"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void honorsLengthRestriction() {
        new PktLine(generateContent(65517));
    }

    @Test
    public void acceptsMaximumLength() {
        new PktLine(generateContent(65516));
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotAcceptEmptyContent() {
        // This would generate an illegal pkt-line according to spec (0004)
        new PktLine("");
    }

    private String generateContent(int length) {
        byte[] content = new byte[length];
        Arrays.fill(content, (byte) 'a');
        return new String(content);
    }

    private void assertOutput(PktLine line, String output) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            line.writeTo(stream);
        } catch (IOException e) {
            throw new RuntimeException("Ups!", e);
        }

        assertArrayEquals(
                output.getBytes(StandardCharsets.US_ASCII),
                stream.toByteArray()
        );
    }
}