package com.github.trulfos.bambooplugins;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.repository.RepositoryDefinitionManager;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.github.trulfos.bambooplugins.git.Ref;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Scanned
public class BitbucketTaggerTaskConfigurator extends AbstractTaskConfigurator {

    private final RepositoryDefinitionManager repoManager;

    @Autowired
    BitbucketTaggerTaskConfigurator(@ComponentImport RepositoryDefinitionManager repoManager) {
        this.repoManager = repoManager;
    }

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull ActionParametersMap params, @Nullable TaskDefinition previousTaskDefinition) {
        Config config = new Config(super.generateTaskConfigMap(params, previousTaskDefinition));

        config.setRef(
                params.getString("tagName"),
                params.getString("revision")
        );
        config.setRepo(params.getString("repo"));

        return config.getConfigurationMap();
    }

    @Override
    public void populateContextForCreate(@NotNull Map<String, Object> context) {
        super.populateContextForCreate(context);

        context.put("revision", "${bamboo.planRepository.1.revision}");
        context.put("tagName", "refs/tags/build-${bamboo.buildNumber}");
        context.put("repos", repoManager.getLinkedRepositories());
    }

    @Override
    public void populateContextForEdit(@NotNull Map<String, Object> context, @NotNull TaskDefinition taskDefinition) {
        super.populateContextForEdit(context, taskDefinition);
        Config config = new Config(taskDefinition.getConfiguration());

        context.put("revision", config.getRefHash());
        context.put("tagName", config.getRefName());
        context.put("repos", repoManager.getLinkedRepositories());
        context.put("repo", config.getRepo());
    }

    @Override
    public void validate(@NotNull ActionParametersMap params, @NotNull ErrorCollection errorCollection) {
        super.validate(params, errorCollection);

        final String revision = params.getString("revision");
        if (revision != null && revision.isEmpty()) {
            errorCollection.addError("revision", "Please specify a revision");
        }

        final String tagName = params.getString("tagName");
        if (tagName != null && tagName.isEmpty()) {
            errorCollection.addError("revision", "Please specify a tag name");
        }

        final String repo = params.getString("repo");
        if (repo != null && repo.isEmpty()) {
            errorCollection.addError("repo", "Please select a repository");
        }
    }
}
