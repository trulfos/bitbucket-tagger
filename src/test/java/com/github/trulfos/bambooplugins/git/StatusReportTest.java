package com.github.trulfos.bambooplugins.git;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatusReportTest {

    @Test(expected = RuntimeException.class)
    public void doesNotAcceptEmtpyArray() {
        createReport();
    }

    @Test
    public void detectsSuccessfulUnpack() {
        StatusReport report = createReport("unpack ok");
        assertTrue(report.isSuccess());
    }

    @Test
    public void detectsUnsuccessfulUnpack() {
        StatusReport report = createReport("unpack fail");
        assertFalse(report.isSuccess());
    }

    @Test
    public void detectsBranchOk() {
        StatusReport report = createReport(
                "unpack ok\n",
                "ng refs/heads/mybranch\n",
                "ok refs/tags/new-tag3\n"
        );

        assertTrue(report.isRefUpdated("refs/tags/new-tag3"));
    }

    @Test
    public void detectsBranchFailed() {
        StatusReport report = createReport(
                "unpack ok\n",
                "ng refs/tags/new-tag2 failed to update ref\n",
                "ok refs/heads/mybranch2\n"
        );

        assertFalse(report.isRefUpdated("refs/tags/new-tag2"));
    }

    @Test
    public void conveysReason() {
        StatusReport report = createReport(
                "unpack ok\n",
                "ng refs/tags/new-tag2 missing necessary objects\n"
        );

        assertEquals("missing necessary objects", report.getRefError("refs/tags/new-tag2"));
    }

    @Test
    public void alwaysReportsErrorForUnsuccessfulUnpack() {
        StatusReport report = createReport(
                "unpack faiil\n",
                "ok refs/heads/mybranch2\n"
        );

        assertFalse(report.isRefUpdated("refs/heads/mybranch2"));
    }

    @Test
    public void givesReasonWhenFailedDueToUnsuccessfulUnpack() {
        StatusReport report = createReport(
                "unpack faoen\n",
                "ok refs/tags/new-tag2\n"
        );

        assertNotNull(report.getRefError("refs/tags/new-tag2"));
    }

    private StatusReport createReport(String... lines) {
        PktLine[] pktLines = new PktLine[lines.length];

        for (int i = 0; i < lines.length; i++) {
            pktLines[i] = new PktLine(lines[i]);
        }

        return new StatusReport(pktLines);
    }
}