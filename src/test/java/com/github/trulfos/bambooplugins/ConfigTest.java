package com.github.trulfos.bambooplugins;

import com.atlassian.bamboo.repository.RepositoryDefinitionManager;
import com.atlassian.bamboo.vcs.configuration.VcsRepositoryData;
import com.github.trulfos.bambooplugins.git.Ref;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigTest {

    @Test
    public void remembersRef() {
        Ref testRef = new Ref("refs/tags/testTag", "708950583b7664e1ec45cd506aeed5e628132be1");
        Map<String, String> configMap = new HashMap<>();

        new Config(configMap).setRef(
                testRef.getName(),
                testRef.getHash()
        );

        assertEquals(
                testRef,
                new Config(configMap).getRef()
        );
    }

    @Test
    public void supportsLegacyConfigFormat() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("tagName", "blabla");
        configMap.put("revision", "708950583b7664e1ec45cd506aeed5e628132be1");

        assertEquals(
                new Ref("refs/tags/blabla", "708950583b7664e1ec45cd506aeed5e628132be1"),
                new Config(configMap).getRef()
        );
    }

    @Test(expected = InvalidConfigException.class)
    public void throwsWhenRefKeysNotPresent() {
        new Config(new HashMap<>()).getRef();
    }

    @Test(expected = InvalidConfigException.class)
    public void throwsRepoKeyNotPresent() {
        new Config(new HashMap<>()).getRepo(null);
    }

    @Test
    public void remembersRepo() {
        String repoName = "test-repo";
        Map<String, String> configMap = new HashMap<>();
        RepositoryDefinitionManager repoManager = mock(RepositoryDefinitionManager.class);
        VcsRepositoryData repo = mock(VcsRepositoryData.class);

        new Config(configMap).setRepo(repoName);

        when(repoManager.getLinkedRepositoryByName("test-repo"))
                .thenReturn(repo);

        assertEquals(
                repo,
                new Config(configMap).getRepo(repoManager)
        );
    }

    @Test(expected = InvalidConfigException.class)
    public void throwsOnNonexistentRepo() {
        String repoName = "test-repo";
        Config config = new Config(new HashMap<>());

        RepositoryDefinitionManager repoManager = mock(RepositoryDefinitionManager.class);
        when(repoManager.getLinkedRepositoryByName("test-repo"))
                .thenReturn(null);

        config.setRepo(repoName);
        config.getRepo(repoManager);
    }
}