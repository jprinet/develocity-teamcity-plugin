package com.gradle.develocity.teamcity.agent;

import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;

public class Logger {

    private static final String CUSTOM_DEVELOCITY_EXTENSION_LOGGER_ENABLED = "develocityPlugin.develocity.extension.logger.enabled";

    private final BuildProgressLogger logger;
    private final boolean isLoggingEnabled;

    public Logger(BuildRunnerContext runner) {
        this.logger = runner.getBuild().getBuildLogger();
        this.isLoggingEnabled = Boolean.parseBoolean(runner.getConfigParameters().get(CUSTOM_DEVELOCITY_EXTENSION_LOGGER_ENABLED));
    }

    void message(String message) {
        if (isLoggingEnabled) {
            logger.message("[DEVELOCITY] " + message);
        }
    }
}
