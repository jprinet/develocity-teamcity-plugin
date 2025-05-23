package com.gradle.develocity.teamcity.agent.maven

import com.gradle.develocity.teamcity.agent.TcPluginConfig
import com.gradle.develocity.teamcity.agent.maven.testutils.GroupArtifactVersion
import com.gradle.develocity.teamcity.agent.maven.testutils.MavenProject

import static org.junit.Assume.assumeTrue

class CustomCoordinatesExtensionApplicationTest extends BaseExtensionApplicationTest {

    def "does not inject Develocity extension when not defined in project but matching custom coordinates defined in project (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue DEVELOCITY_URL != null

        given:
        def mvnProject = new MavenProject.Configuration(
            develocityUrl: DEVELOCITY_URL,
            // using Guava as surrogate since we do not have a custom extension at hand that pulls in the Develocity Maven extension transitively
            customExtension: new GroupArtifactVersion(group: 'com.google.guava', artifact: 'guava', version: '31.1-jre')
        ).buildIn(checkoutDir)

        and:
        def develocityPluginConfig = new TcPluginConfig(
            enableInjection: true,
            develocityUrl: DEVELOCITY_URL,
            develocityExtensionVersion: DEVELOCITY_EXTENSION_VERSION,
            develocityExtensionCustomCoordinates: 'com.google.guava:guava',
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, develocityPluginConfig)

        then:
        0 * extensionApplicationListener.develocityExtensionApplied(_)
        0 * extensionApplicationListener.ccudExtensionApplied(_)

        and:
        outputMissesTeamCityServiceMessageBuildStarted(output)
        outputMissesTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << SUPPORTED_MAVEN_VERSIONS
    }

    def "does not inject CCUD extension when not defined in project but matching custom coordinates defined in project (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue DEVELOCITY_URL != null

        given:
        def mvnProject = new MavenProject.Configuration(
            develocityUrl: DEVELOCITY_URL,
            develocityExtensionVersion: DEVELOCITY_EXTENSION_VERSION,
            // using Guava as surrogate since we do not have a custom extension at hand that pulls in the GE Maven extension transitively
            customExtension: new GroupArtifactVersion(group: 'com.google.guava', artifact: 'guava', version: '31.1-jre')
        ).buildIn(checkoutDir)

        and:
        def develocityPluginConfig = new TcPluginConfig(
            enableInjection: true,
            develocityUrl: DEVELOCITY_URL,
            ccudExtensionVersion: CCUD_EXTENSION_VERSION,
            ccudExtensionCustomCoordinates: 'com.google.guava:guava',
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, develocityPluginConfig)

        then:
        0 * extensionApplicationListener.develocityExtensionApplied(_)
        0 * extensionApplicationListener.ccudExtensionApplied(_)

        and:
        outputContainsTeamCityServiceMessageBuildStarted(output)
        outputContainsTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << SUPPORTED_MAVEN_VERSIONS
    }
}
