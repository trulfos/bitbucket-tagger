package com.github.trulfos.bambooplugins;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BitbucketTaggerTaskConfigurator extends AbstractTaskConfigurator {
    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull ActionParametersMap params, @Nullable TaskDefinition previousTaskDefinition) {
        Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);

        config.put("revision", params.getString("revision"));
        config.put("tagName", params.getString("tagName"));

        return config;
    }

    @Override
    public void populateContextForCreate(@NotNull Map<String, Object> context) {
        super.populateContextForCreate(context);

        context.put("revision", "${bamboo.planRepository.1.revision}");
        context.put("tagName", "build-${bamboo.buildNumber}");
    }

    @Override
    public void populateContextForEdit(@NotNull Map<String, Object> context, @NotNull TaskDefinition taskDefinition) {
        super.populateContextForEdit(context, taskDefinition);
        Map<String, String> config = taskDefinition.getConfiguration();

        context.put("revision", config.get("revision"));
        context.put("tagName", config.get("tagName"));
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
    }
}
