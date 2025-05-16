package com.gradle.develocity.teamcity.agent.maven

import com.gradle.develocity.teamcity.agent.TcPluginConfig
import com.gradle.develocity.teamcity.agent.maven.testutils.MavenProject

import static org.junit.Assume.assumeTrue

class ServiceMessageExtensionApplicationTest extends BaseExtensionApplicationTest {

    def "build succeeds when service message maven extension is applied to a project without Develocity in the extension classpath (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue BaseExtensionApplicationTest.DEVELOCITY_URL != null

        given:
        def mvnProject = new MavenProject.Configuration().buildIn(checkoutDir)

        and:
        def develocityPluginConfig = new TcPluginConfig(
            enableInjection: true,
            enableCommandLineRunner: true,
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, develocityPluginConfig)

        then:
        outputContainsBuildSuccess(output)
        outputMissesTeamCityServiceMessageBuildStarted(output)
        outputMissesTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << BaseExtensionApplicationTest.SUPPORTED_MAVEN_VERSIONS
    }
}
