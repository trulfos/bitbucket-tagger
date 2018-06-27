package com.github.trulfos.bambooplugins.git;

import org.junit.Test;

import static org.junit.Assert.*;

public class RepoUrlTest {

    @Test
    public void extractsExplicitProtocol() {
        new Check("ssh://test.example.com/mitt/repo").protocol("ssh");
        new Check("git://test.example.com/mitt/repo").protocol("git");
        new Check("git+ssh://test.example.com/mitt/repo").protocol("git+ssh");
    }

    @Test
    public void extractsUsername() {
        new Check("ssh://truls@blabla.com/mitt/repo").user("truls");
        new Check("martin@blabla.com:mitt/repo").user("martin");
    }

    @Test
    public void allowsSkippingUsername() {
        new Check("ssh://test.example.com:8080/mitt/repo")
                .user(null);
    }

    @Test
    public void assumesPort22ForSsh() {
        new Check("ssh://test.com/repo").port(22);
    }

    @Test
    public void assumesPort0ForOtherProtocols() {
        new Check("meh://blabla.com/onehu").port(0);
    }

    @Test
    public void extractsPort() {
        new Check("ssh://test.com:8800/meh").port(8800);
        new Check("ssh://blab@test.com:1234/meh").port(1234);
    }

    @Test
    public void extractsRepo() {
        new Check("ssh://blabl.com/mitt/repo").repo("/mitt/repo");
        new Check("ssh://truls@blabl.com/mitt/repo").repo("/mitt/repo");
        new Check("truls@blabl.com:/mitt/repo").repo("/mitt/repo");
        new Check("ssh://truls@blabl.com:233/mitt/repo").repo("/mitt/repo");
    }

    @Test
    public void removesFirstCharForRelativeRepos() {
        new Check("ssh://bloeu.com:8080/~relative/repo").repo("~relative/repo");
        new Check("oeu@bloeu.com:relative/repo").repo("relative/repo");
    }

    @Test
    public void allowsFileUrl() {
        new Check("meh/blabla").repo("meh/blabla");
        new Check("meh").repo("meh");
    }

    @Test
    public void extractsHost() {
        new Check("ssh://example.com/repo").host("example.com");
        new Check("meh://192.168.0.1/repo").host("192.168.0.1");
        new Check("meh@gaagle.com:repo").host("gaagle.com");
    }

    @Test
    public void handlesImplicitSsh() {
        new Check("meh@meh.com:bleble/bth.git").protocol("ssh");
    }

    @Test
    public void handlesLocalPaths() {
        new Check("/blabla/netu.git")
                .repo("/blabla/netu.git")
                .protocol(null)
                .host(null)
                .user(null);
    }

    private static class Check {
        private RepoUrl url;

        Check(String url) {
            this.url = new RepoUrl(url);
        }

        Check protocol(String protocol) {
            assertEquals(protocol, url.getProtocol());
            return this;
        }

        Check user(String user) {
            assertEquals(user, url.getUser());
            return this;
        }

        Check host(String host) {
            assertEquals(host, url.getHost());
            return this;
        }

        Check port(int port) {
            assertEquals(port, url.getPort());
            return this;
        }

        Check repo(String repo) {
            assertEquals(repo, url.getRepo());
            return this;
        }
    }
}