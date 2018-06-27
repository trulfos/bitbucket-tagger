package com.github.trulfos.bambooplugins.git;

public class NewTagPktLine extends PktLine {
    public NewTagPktLine(Ref ref) {
        super("0000000000000000000000000000000000000000 " + ref.getHash() + " " + ref.getName());
    }
}
