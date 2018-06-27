package com.github.trulfos.bambooplugins.git;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PackReader {
    DataInputStream stream;

    public PackReader(InputStream stream) {
        if (stream == null) {
            throw new NullPointerException();
        }

        this.stream = new DataInputStream(stream);
    }

    public PktLine[] readPktLines() throws IOException {
        List<PktLine> lines = new ArrayList<>();

        PktLine line = new PktLine(stream);
        while (!line.isFlush()) {
            lines.add(line);
            line = new PktLine(stream);
        }

        return lines.toArray(new PktLine[0]);
    }
}
