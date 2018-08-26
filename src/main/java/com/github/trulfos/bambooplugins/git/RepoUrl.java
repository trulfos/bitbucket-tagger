package com.github.trulfos.bambooplugins.git;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepoUrl {
    private static final Pattern pattern = Pattern.compile(
            "^(?:" +
                "(?<protocol>[^:/]+)://" +
                "(?:(?<user>[^:@/]+)@)?" +
                "(?<host>[^:/]+)" +
                "(?::(?<port>[0-9]{1,6}))?" +
                "(?<separator>[/:])" +
            ")?(?<repo>.*)$"
    );

    private Matcher matcher;


    public RepoUrl(String url) {
        String prefix = url.matches("^[^/:]+@[^/:]+:.*$")
                    ? "ssh://"
                    : "";

        matcher = pattern.matcher(prefix + url);

        if (!matcher.matches()) {
            throw new InvalidGitRepoException("Invalid git repo url");
        }
    }

    public String getProtocol() {
        String proto = this.matcher.group("protocol");

        if (proto == null) {
            return getHost() == null
                    ? null
                    : "ssh";
        }

        return proto;
    }

    public String getUser() {
        return matcher.group("user");
    }

    public String getRepo() {
        String separator = matcher.group("separator");
        String repo = matcher.group("repo");

        if (isRelativeRepo(separator, repo)) {
            return separator + repo;
        }

        return repo;
    }

    public String getHost() {
        return matcher.group("host");
    }

    public short getPort() {
        String match = matcher.group("port");

        if (match == null) {
            return "ssh".equals(getProtocol())
                    ? (short) 22
                    : 0;
        }

        return Short.parseShort(match, 10);
    }

    private boolean isRelativeRepo(String separator, String repo) {
        return "/".equals(separator) && !repo.startsWith("~");
    }

    public String toString() {
        return getProtocol() + "://" +
                getUser() + "@" + getHost() + ":" + getPort() +
                getRepo();
    }
}
