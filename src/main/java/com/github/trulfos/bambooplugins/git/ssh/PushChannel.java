package com.github.trulfos.bambooplugins.git.ssh;

import com.github.trulfos.bambooplugins.git.PackReader;
import com.github.trulfos.bambooplugins.git.PackWriter;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.Closeable;
import java.io.IOException;

class PushChannel implements Closeable {
    private ChannelExec channel;

    PushChannel(Session jschSession, String repo) {
        try {
            establishChannel(jschSession, repo);
        } catch (JSchException e) {
            throw new RuntimeException("Could not establish ssh exec channel", e);
        }
    }

    PackReader getReader() throws IOException {
        return new PackReader(channel.getInputStream());
    }

    PackWriter getWriter() throws IOException {
        return new PackWriter(channel.getOutputStream(), "report-status");
    }

    @Override
    public void close() throws IOException {
        channel.disconnect();
    }

    private void establishChannel(Session jschSession, String repo) throws JSchException {
        channel = (ChannelExec) jschSession.openChannel("exec");
        channel.setCommand("git-receive-pack '" + repo + "'"); // TODO: Escape
        channel.connect();
    }
}
