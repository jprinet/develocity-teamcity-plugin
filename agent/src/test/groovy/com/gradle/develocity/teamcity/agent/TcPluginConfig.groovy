package com.gradle.develocity.teamcity.agent

class TcPluginConfig {

    URI gradlePluginRepositoryUrl
    String gradlePluginRepositoryUsername
    String gradlePluginRepositoryPassword
    URI develocityUrl
    boolean develocityAllowUntrustedServer
    boolean enableInjection
    boolean develocityEnforceUrl
    boolean develocityPluginFileFingerprints
    String develocityPluginVersion
    String ccudPluginVersion
    String develocityExtensionVersion
    String ccudExtensionVersion
    String develocityExtensionCustomCoordinates
    String ccudExtensionCustomCoordinates
    boolean develocityExtensionFileFingerprints
    boolean enableCommandLineRunner

    // configuration params as they would be set by the user in the TeamCity configuration
    Map<String, String> toConfigParameters() {
        Map<String, String> configProps = [:]
        if (gradlePluginRepositoryUrl) {
            configProps.put 'develocityPlugin.gradle.plugin-repository.url', gradlePluginRepositoryUrl.toString()
        }
        if (develocityUrl) {
            configProps.put 'develocityPlugin.develocity.url', develocityUrl.toString()
        }
        if (develocityAllowUntrustedServer) {
            configProps.put 'develocityPlugin.develocity.allow-untrusted-server', 'true'
        }
        if (enableInjection) {
            configProps.put 'develocityPlugin.develocity.injection.enabled', 'true'
        }
        if (develocityEnforceUrl) {
            configProps.put 'develocityPlugin.develocity.enforce-url', 'true'
        }
        if (develocityPluginVersion) {
            configProps.put 'develocityPlugin.develocity.plugin.version', develocityPluginVersion
        }
        if (ccudPluginVersion) {
            configProps.put 'develocityPlugin.ccud.plugin.version', ccudPluginVersion
        }
        if (develocityExtensionVersion) {
            configProps.put 'develocityPlugin.develocity.extension.version', develocityExtensionVersion
        }
        if (ccudExtensionVersion) {
            configProps.put 'develocityPlugin.ccud.extension.version', ccudExtensionVersion
        }
        if (develocityExtensionCustomCoordinates) {
            configProps.put('develocityPlugin.develocity.extension.custom.coordinates', develocityExtensionCustomCoordinates)
        }
        if (ccudExtensionCustomCoordinates) {
            configProps.put('develocityPlugin.ccud.extension.custom.coordinates', ccudExtensionCustomCoordinates)
        }
        if (enableCommandLineRunner) {
            configProps.put 'develocityPlugin.command-line-build-step.enabled', 'true'
        }
        configProps.put 'develocityPlugin.gradle.plugin.capture-file-fingerprints', develocityPluginFileFingerprints as String
        configProps.put 'develocityPlugin.develocity.extension.capture-file-fingerprints', develocityExtensionFileFingerprints as String

        configProps
    }

    Map<String, String> toEnvVars() {
        Map<String, String> envs = [:]
        if (gradlePluginRepositoryUsername) {
            envs.put 'DEVELOCITY_INJECTION_PLUGIN_REPOSITORY_USERNAME', gradlePluginRepositoryUsername
        }
        if (gradlePluginRepositoryPassword) {
            envs.put 'DEVELOCITY_INJECTION_PLUGIN_REPOSITORY_PASSWORD', gradlePluginRepositoryPassword
        }
        envs
    }

}
