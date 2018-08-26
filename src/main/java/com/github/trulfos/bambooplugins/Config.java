package com.github.trulfos.bambooplugins;

import com.atlassian.bamboo.repository.RepositoryDefinitionManager;
import com.atlassian.bamboo.vcs.configuration.VcsRepositoryData;
import com.github.trulfos.bambooplugins.git.InvalidHashException;
import com.github.trulfos.bambooplugins.git.InvalidRefNameException;
import com.github.trulfos.bambooplugins.git.Ref;

import java.util.HashMap;
import java.util.Map;

class Config {
    private Map<String, String> configurationMap;

    private static final String REVISION_KEY = "revision";
    private static final String TAG_NAME_KEY = "tagName";
    private static final String REPO_KEY = "repo";

    Config(Map<String, String> configurationMap) {
        this.configurationMap = configurationMap;
    }

    void setRef(String name, String hash) {
        configurationMap.put(REVISION_KEY, hash);
        configurationMap.put(TAG_NAME_KEY, name);
    }

    String getRefName() {
        return configurationMap.get(TAG_NAME_KEY);
    }

    String getRefHash() {
        return configurationMap.get(REVISION_KEY);
    }

    Ref getRef() {
        String revision = configurationMap.get(REVISION_KEY);
        String tagName = configurationMap.get(TAG_NAME_KEY);

        if (revision == null || tagName == null) {
            throw new InvalidConfigException("Missing keys in config map");
        }

        try {
            return new Ref(
                    tagName.contains("/")
                            ? tagName
                            : "refs/tags/" + tagName,
                    revision
            );
        } catch (InvalidRefNameException|InvalidHashException e) {
            throw new InvalidConfigException("Invalid ref in config", e);
        }
    }

    void setRepo(String repoName) {
        configurationMap.put(REPO_KEY, repoName);
    }

    String getRepo() {
        return configurationMap.get(REPO_KEY);
    }

    VcsRepositoryData getRepo(RepositoryDefinitionManager repoManager) {
        String name = configurationMap.get(REPO_KEY);

        if (name == null) {
            throw new InvalidConfigException("No repo set in configuration");
        }

        VcsRepositoryData data = repoManager.getLinkedRepositoryByName(name);

        if (data == null) {
            throw new InvalidConfigException("Could not find the repository " + name);
        }

        return data;
    }

    Map<String, String> getConfigurationMap() {
        return new HashMap<>(configurationMap);
    }
}
