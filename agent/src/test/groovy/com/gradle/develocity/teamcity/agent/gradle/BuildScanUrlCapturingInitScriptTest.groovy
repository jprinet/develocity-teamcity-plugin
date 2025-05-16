package com.gradle.develocity.teamcity.agent.gradle

import com.gradle.develocity.teamcity.agent.TcPluginConfig

import static org.junit.Assume.assumeTrue

class BuildScanUrlCapturingInitScriptTest extends BaseInitScriptTest {

    def "sends build started service message even without declaring Develocity / Build Scan plugin (#jdkCompatibleGradleVersion)"() {
        assumeTrue jdkCompatibleGradleVersion.isJvmVersionCompatible()

        when:
        def result = run(jdkCompatibleGradleVersion.gradleVersion, new TcPluginConfig(enableInjection: true))

        then:
        outputContainsTeamCityServiceMessageBuildStarted(result)

        where:
        jdkCompatibleGradleVersion << BaseInitScriptTest.GRADLE_VERSIONS_3_0_AND_HIGHER
    }

    def "send build scan url service message when declaring Develocity / Build Scan plugin (#jdkCompatibleGradleVersion)"() {
        assumeTrue jdkCompatibleGradleVersion.isJvmVersionCompatible()

        given:
        declareDevelocityPluginApplication(jdkCompatibleGradleVersion.gradleVersion)

        when:
        def result = run(jdkCompatibleGradleVersion.gradleVersion, new TcPluginConfig(enableInjection: true))

        then:
        outputContainsTeamCityServiceMessageBuildStarted(result)
        outputContainsTeamCityServiceMessageBuildScanUrl(result)

        where:
        jdkCompatibleGradleVersion << BaseInitScriptTest.GRADLE_VERSIONS_3_0_AND_HIGHER
    }

}
