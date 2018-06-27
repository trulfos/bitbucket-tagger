package com.github.trulfos.bambooplugins.git.ssh;

import com.jcraft.jsch.*;

import java.nio.charset.StandardCharsets;

public class KeyBasedJsch {
    private String host;
    private int port;
    private JSch jSch;

    KeyBasedJsch(String host, String privateKey, String hostKey, int port) throws JSchException {
        this.host = host;
        this.port = port;

        // TODO: Find out how to get the host key in Bamboo
        JSch.setConfig("StrictHostKeyChecking", "no");

        jSch = new JSch();
        loadHostKey(hostKey);
        loadPrivateKey(privateKey);
    }

    public Session getSession(String username) throws JSchException {
        return jSch.getSession(username, host, port);
    }

    private void loadHostKey(String key) throws JSchException {
        byte[] keyBlob = KeyPair
                .load(
                        jSch,
                        null,
                        key.getBytes(StandardCharsets.UTF_8)
                )
                .getPublicKeyBlob();

        jSch.getHostKeyRepository()
                .add(
                        new HostKey(host, keyBlob),
                        null
                );
    }

    private void loadPrivateKey(String key) throws JSchException {
        jSch.addIdentity(
                "repokey",
                key.getBytes(StandardCharsets.UTF_8),
                null,
                null
        );
    }
}
