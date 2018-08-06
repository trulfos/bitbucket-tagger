package com.github.trulfos.bambooplugins;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.repository.RepositoryDefinitionManager;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
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
        Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);

        config.put("revision", params.getString("revision"));
        config.put("tagName", params.getString("tagName"));
        config.put("repo", params.getString("repo"));

        return config;
    }

    @Override
    public void populateContextForCreate(@NotNull Map<String, Object> context) {
        super.populateContextForCreate(context);

        context.put("revision", "${bamboo.planRepository.1.revision}");
        context.put("tagName", "build-${bamboo.buildNumber}");
        context.put("repos", repoManager.getLinkedRepositories());
    }

    @Override
    public void populateContextForEdit(@NotNull Map<String, Object> context, @NotNull TaskDefinition taskDefinition) {
        super.populateContextForEdit(context, taskDefinition);
        Map<String, String> config = taskDefinition.getConfiguration();

        context.put("revision", config.get("revision"));
        context.put("tagName", config.get("tagName"));
        context.put("repos", repoManager.getLinkedRepositories());
        context.put("repo", config.get("repo"));
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
