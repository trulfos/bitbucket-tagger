package com.github.trulfos.bambooplugins;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.*;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.github.trulfos.bambooplugins.git.Ref;
import com.github.trulfos.bambooplugins.git.ssh.GitProtcolError;
import com.github.trulfos.bambooplugins.git.ssh.RefNotUpdatedException;
import com.github.trulfos.bambooplugins.git.ssh.UnsupportedProtocolException;
import org.jetbrains.annotations.NotNull;

import com.atlassian.bamboo.repository.RepositoryDefinitionManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

@Scanned
public class BitbucketTaggerTask implements CommonTaskType
{
    private static final String UNSUPPORTED_PROTOCOL = "This plugin currently " +
            "only supports communication with Bitbucket using SSH, but the " +
            "provided repository definition contains a repository URL " +
            "suggesting another protocol. Please change the definition or " +
            "improve this plugin.";

    private static final String INVALID_CONFIG = "The stored configuration " +
            "is invalid. This may happen when injecting variables result in " +
            "corrupt configuration values (such as tag name), or when the " +
            "linked repository is deleted after configuring the plugin.";

    private static final String IO_ERROR = "Seems like an IOException has " +
            "occured. We're very sorry, but a stack trace is the best help " +
            "we can give you at the moment :(";

    private static final String PROTOCOL_PROBLEMS = "A protocol error " +
            "has occured. This may be caused by a server talking a strange " +
            "language this plugin does not understand (different git version?)";

    private static final String REF_NOT_UPDATED = "The ref could not be " +
            "updated on the server. This is likely to be because the ref " +
            "already exists, or because the referenced commit is not present " +
            "on the server side.";

    private final RepositoryDefinitionManager repoManager;

    @Autowired
    public BitbucketTaggerTask(
            @ComponentImport RepositoryDefinitionManager repoManager
    ) {
        this.repoManager = repoManager;
    }

    @Override
    public TaskResult execute(@NotNull CommonTaskContext taskContext)
            throws TaskException
    {
        BuildLogger logger = taskContext.getBuildLogger();
        Config config = new Config(taskContext.getConfigurationMap());
        Ref ref = config.getRef();

        Map<String, String> locationConfig = config
                .getRepo(repoManager)
                .getVcsLocation()
                .getConfiguration();

        try (
                BitbucketSshPushConnection connection =
                        new BitbucketSshPushConnection(locationConfig)
        ) {
            logger.addBuildLogEntry(
                    "Trying to push " + ref.getHash() + " as " +
                            ref.getName() + " to " + connection.getUrl()
            );

            connection.push(ref);

            logger.addBuildLogEntry(
                    "Successfully pushed tag to " + connection.getUrl()
            );
        } catch (IOException e) {
            throw new TaskException(IO_ERROR, e);
        } catch (InvalidConfigException e) {
            throw new TaskException(INVALID_CONFIG, e);
        } catch (UnsupportedProtocolException e) {
            throw new TaskException(UNSUPPORTED_PROTOCOL, e);
        } catch (RefNotUpdatedException e) {
            throw new TaskException(REF_NOT_UPDATED, e);
        } catch (GitProtcolError e) {
            throw new TaskException(PROTOCOL_PROBLEMS, e);
        }

        return TaskResultBuilder.newBuilder(taskContext)
                .success()
                .build();
    }

}
