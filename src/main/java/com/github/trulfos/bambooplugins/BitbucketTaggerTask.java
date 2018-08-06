package com.github.trulfos.bambooplugins;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.vcs.configuration.VcsRepositoryData;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.github.trulfos.bambooplugins.git.RepoUrl;
import com.github.trulfos.bambooplugins.git.Ref;
import com.github.trulfos.bambooplugins.git.ssh.SshPushConnection;
import org.jetbrains.annotations.NotNull;

import com.atlassian.bamboo.repository.RepositoryDefinitionManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

@Scanned
public class BitbucketTaggerTask implements CommonTaskType
{
    private final RepositoryDefinitionManager repoManager;

    @Autowired
    public BitbucketTaggerTask(@ComponentImport RepositoryDefinitionManager repoManager) {
        this.repoManager = repoManager;
    }

    @Override
    public TaskResult execute(@NotNull CommonTaskContext taskContext) throws TaskException {
        BuildLogger logger = taskContext.getBuildLogger();
        Map<String, String> config = taskContext.getConfigurationMap();
        Ref ref = getRef(config);
        VcsRepositoryData repo = getRepo(config);

        Map<String, String> locationConfig = repo.getVcsLocation().getConfiguration();
        String hostKey = locationConfig.get("repository.stash.key.public");
        String privateKey = locationConfig.get("repository.stash.key.private");
        String repoUrl = locationConfig.get("repository.stash.repositoryUrl");

        if (repoUrl == null) {
            throw new TaskException("Found repository without repo url");
        }

        if (hostKey == null) {
            throw new TaskException("No host key for repository " + repoUrl);
        }

        if (privateKey == null) {
            throw new TaskException("No private key for repository " + repoUrl);
        }

        logger.addBuildLogEntry(
                "Trying to push " + ref.getHash() + " as " + ref.getName() + " to " + repoUrl
        );

        RepoUrl url = new RepoUrl(repoUrl);

        try (SshPushConnection connection = new SshPushConnection(url, privateKey, hostKey)) {
            connection.push(ref);
        } catch (IOException e) {
            throw new TaskException("IOException when pushing ref", e);
        }

        logger.addBuildLogEntry("Successfully pushed tag to " + repoUrl);
        return TaskResultBuilder.newBuilder(taskContext).success().build();
    }

    private VcsRepositoryData getRepo(Map<String, String> config) throws TaskException {
        String name = config.get("repo");

        if (name == null) {
            throw new TaskException("No repo set in configuration");
        }

        VcsRepositoryData data = this.repoManager.getLinkedRepositoryByName(name);

        if (data == null) {
            throw new TaskException("Could not find the repository " + name);
        }

        return data;
    }

    private Ref getRef(Map<String, String> config) throws TaskException {
        String revision = config.get("revision");
        String tagName = config.get("tagName");

        if (revision == null || revision.isEmpty()) {
            throw new TaskException("No revision given. Please check the tagger task configuration.");
        }

        if (tagName == null || tagName.isEmpty()) {
            throw new TaskException("No tag name given. Please check the tagger task configuration.");
        }

        return new Ref("refs/tags/" + tagName, revision);
    }
}
