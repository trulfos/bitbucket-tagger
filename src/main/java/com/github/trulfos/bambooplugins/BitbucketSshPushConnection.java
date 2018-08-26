package com.github.trulfos.bambooplugins;

import com.github.trulfos.bambooplugins.git.Ref;
import com.github.trulfos.bambooplugins.git.RepoUrl;
import com.github.trulfos.bambooplugins.git.ssh.SshPushConnection;

import java.io.IOException;
import java.util.Map;

public class BitbucketSshPushConnection implements AutoCloseable {
    private SshPushConnection delegate;

    BitbucketSshPushConnection(Map<String, String> locationConfig) {
        String hostKey = locationConfig.get("repository.stash.key.public");
        String privateKey = locationConfig.get("repository.stash.key.private");
        String repoUrl = locationConfig.get("repository.stash.repositoryUrl");

        if (repoUrl == null) {
            throw new UnsupportedVcsLocationConfigException("Found repository without repo url");
        }

        if (hostKey == null) {
            throw new UnsupportedVcsLocationConfigException("No host key for repository " + repoUrl);
        }

        if (privateKey == null) {
            throw new UnsupportedVcsLocationConfigException("No private key for repository " + repoUrl);
        }

        this.delegate = new SshPushConnection(
                new RepoUrl(repoUrl),
                privateKey,
                hostKey
        );
    }

    RepoUrl getUrl() {
        return delegate.getUrl();
    }

    void push(Ref ref) throws IOException {
        delegate.push(ref);
    }

    @Override
    public void close() {
        delegate.close();
    }
}
