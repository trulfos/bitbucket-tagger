package com.github.trulfos.bambooplugins;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.vcs.configuration.PlanRepositoryDefinition;
import com.github.trulfos.bambooplugins.git.RepoUrl;
import com.github.trulfos.bambooplugins.git.Ref;
import com.github.trulfos.bambooplugins.git.ssh.SshPushConnection;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

public class BitbucketTaggerTask implements CommonTaskType
{

    @Override
    public TaskResult execute(@NotNull CommonTaskContext taskContext) throws TaskException {
        BuildLogger logger = taskContext.getBuildLogger();
        boolean didPush = false;
        Ref ref = getRef(taskContext.getConfigurationMap());

        for (PlanRepositoryDefinition d : taskContext.getCommonContext().getVcsRepositories()) {
            Map<String, String> locationConfig = d.getVcsLocation().getConfiguration();
            String hostKey = locationConfig.get("repository.stash.key.public");
            String privateKey = locationConfig.get("repository.stash.key.private");
            String repoUrl = locationConfig.get("repository.stash.repositoryUrl");

            if (repoUrl == null) {
                logger.addBuildLogEntry("Found repository without repo url");
            }

            if (hostKey == null) {
                logger.addBuildLogEntry("No host key for repository " + repoUrl);
            }

            if (privateKey == null) {
                logger.addBuildLogEntry("No private key for repository " + repoUrl);
            }

            RepoUrl url = new RepoUrl(repoUrl);

            try (SshPushConnection connection = new SshPushConnection(url, privateKey, hostKey)) {
                connection.push(ref);
            } catch (IOException e) {
                throw new TaskException("IOException when pushing ref", e);
            }

            didPush = true;
            logger.addBuildLogEntry("Successfully pushed tag to " + repoUrl);
        }

        if (!didPush) {
            throw new TaskException(
                    "There were no repositories available to which a tag could be pushed."
            );
        }

        return TaskResultBuilder.newBuilder(taskContext).success().build();
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
