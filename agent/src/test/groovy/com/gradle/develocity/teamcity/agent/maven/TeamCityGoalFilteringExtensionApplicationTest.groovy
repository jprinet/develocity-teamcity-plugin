package com.gradle.develocity.teamcity.agent.maven

import com.gradle.develocity.teamcity.agent.TcPluginConfig
import com.gradle.develocity.teamcity.agent.maven.testutils.MavenBuildStepConfig
import com.gradle.develocity.teamcity.agent.maven.testutils.MavenProject

import static org.junit.Assume.assumeTrue

class TeamCityGoalFilteringExtensionApplicationTest extends BaseExtensionApplicationTest {

    def "does not publish build scan for TeamCity specific info goal invocation (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue BaseExtensionApplicationTest.DEVELOCITY_URL != null

        given:
        def mvnProject = new MavenProject.Configuration().buildIn(checkoutDir)

        and:
        def develocityPluginConfig = new TcPluginConfig(
            develocityUrl: BaseExtensionApplicationTest.DEVELOCITY_URL,
            develocityExtensionVersion: BaseExtensionApplicationTest.DEVELOCITY_EXTENSION_VERSION,
        )

        and:
        def mvnBuildStepConfig = new MavenBuildStepConfig(
            checkoutDir: checkoutDir,
            goals: 'org.jetbrains.maven:info-maven3-plugin:1.0.2:info',
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, develocityPluginConfig, mvnBuildStepConfig)

        then:
        outputMissesTeamCityServiceMessageBuildStarted(output)
        outputMissesTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << BaseExtensionApplicationTest.SUPPORTED_MAVEN_VERSIONS
    }

}
