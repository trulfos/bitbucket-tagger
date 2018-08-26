package com.github.trulfos.bambooplugins.git.ssh;

import com.github.trulfos.bambooplugins.git.*;
import com.jcraft.jsch.*;

import java.io.Closeable;
import java.io.IOException;

public class SshPushConnection implements Closeable {
    private RepoUrl url;
    private Session session;

    public SshPushConnection(RepoUrl url, String privateKey, String hostKey) {
        this.url = url;

        String protocol = url.getProtocol();
        if (!"ssh".equals(protocol)) {
            throw new UnsupportedProtocolException(protocol);
        }

        try {
            createSession(privateKey, hostKey);
        } catch (JSchException e) {
            throw new RuntimeException("Could not establish session", e);
        }
    }

    public void push(Ref ref) throws IOException {
        // Open an exec channel
        try (
            PushChannel channel = new PushChannel(session, url.getRepo())
        ) {

            PackReader reader = channel.getReader();
            PackWriter writer = channel.getWriter();

            // Read initial ref list from server
            try {
                reader.readPktLines();
            } catch (IOException e) {
                throw new GitProtcolError("Could not read initial ref list", e);
            }

            // Write update
            writer.write(new NewTagPktLine(ref));
            writer.write(new Pack());

            // Read status report
            StatusReport report;
            try {
                report = new StatusReport(reader.readPktLines());
            } catch (IOException e) {
                throw new GitProtcolError("Failed to read status report", e);
            }

            if (!report.isRefUpdated(ref.getName())) {
                throw new RefNotUpdatedException(
                        report.getRefError(ref.getName())
                );
            }
        }
    }

    private void createSession(String privateKey, String hostKey) throws JSchException {
            session = new KeyBasedJsch(
                    url.getHost(),
                    privateKey,
                    hostKey,
                    url.getPort()
                )
                .getSession(url.getUser());

            session.connect();
    }

    @Override
    public void close() {
        session.disconnect();
    }

    public RepoUrl getUrl() {
        return url;
    }
}
