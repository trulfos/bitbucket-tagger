package com.github.trulfos.bambooplugins.git;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatusReport {
    private static Pattern branchStatus = Pattern.compile(
            "(?<status>(ok|ng)) (?<ref>[^ ]+)( (?<error>.*))?\n?"
    );
    private boolean success;
    private Map<String, String> refErrors = new HashMap<>();

    public StatusReport(PktLine[] lines) {
        if (lines.length < 1) {
            throw new RuntimeException("No status report received from server");
        }

        this.success = lines[0].getContent().startsWith("unpack ok");

        for (int i = 1; i < lines.length; i++) {
            readBranch(lines[i]);
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isRefUpdated(String refname) {
        return success && refErrors.get(refname) == null;
    }

    public String getRefError(String refname) {
        if (!success) {
            return "unpack unsuccessful";
        }

        return refErrors.get(refname);
    }

    private void readBranch(PktLine line) {
        String content = line.getContent();
        Matcher matcher = branchStatus.matcher(content);

        if (!matcher.matches()) {
            throw new RuntimeException("Invalid branch line: " + content);
        }

        String status = matcher.group("status");

        refErrors.put(
                matcher.group("ref"),
                "ok".equals(status)
                    ? null
                    : matcher.group("error")
        );
    }
}
