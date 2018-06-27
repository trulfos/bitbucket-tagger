package com.github.trulfos.bambooplugins.git;

import java.io.IOException;
import java.io.OutputStream;

public class PackWriter {
    private final String[] extraParams;
    private boolean isFirstLine = true;
    private OutputStream stream;

    public PackWriter(OutputStream stream, String ...extraParams) {
        if (stream == null) {
            throw new NullPointerException("Output stream cannot be null");
        }

        this.stream = stream;
        this.extraParams = extraParams;
    }

    public void write(PktLine ...lines) throws IOException {
        for (PktLine line : lines) {

            if (isFirstLine) {
                new PktLine(line.getContent() + getExtraParams()).writeTo(stream);

                isFirstLine = false;
                continue;
            }

            line.writeTo(stream);
        }

        writeFlushPkt();
        stream.flush();
    }

    public void write(Pack pack) throws IOException {
        pack.writeTo(stream);
        stream.flush();
    }

    private String getExtraParams() {
        if (extraParams == null || extraParams.length == 0) {
            return "";
        }

        return "\u0000" + String.join(" ", extraParams);
    }

    private void writeFlushPkt() throws IOException {
        new PktLine().writeTo(stream);
    }
}
