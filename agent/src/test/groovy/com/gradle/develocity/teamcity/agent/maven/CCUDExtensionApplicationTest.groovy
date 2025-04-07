package com.gradle.develocity.teamcity.agent.maven

import com.gradle.develocity.teamcity.agent.TcPluginConfig
import com.gradle.develocity.teamcity.agent.maven.testutils.MavenProject

import static org.junit.Assume.assumeTrue

class CCUDExtensionApplicationTest extends BaseExtensionApplicationTest {

    def "applies CCUD extension via classpath when not defined in project where Develocity extension not defined in project and not applied via classpath (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue BaseExtensionApplicationTest.DEVELOCITY_URL != null

        given:
        def mvnProject = new MavenProject.Configuration().buildIn(checkoutDir)

        and:
        def develocityPluginConfig = new TcPluginConfig(
            develocityUrl: BaseExtensionApplicationTest.DEVELOCITY_URL,
            ccudExtensionVersion: BaseExtensionApplicationTest.CCUD_EXTENSION_VERSION,
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, develocityPluginConfig)

        then:
        0 * extensionApplicationListener.develocityExtensionApplied(_)
        1 * extensionApplicationListener.ccudExtensionApplied(BaseExtensionApplicationTest.CCUD_EXTENSION_VERSION)

        and:
        outputMissesTeamCityServiceMessageBuildStarted(output)
        outputMissesTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << BaseExtensionApplicationTest.SUPPORTED_MAVEN_VERSIONS
    }

    def "applies CCUD extension via classpath when not defined in project where Develocity extension applied via classpath (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue BaseExtensionApplicationTest.DEVELOCITY_URL != null

        given:
        def mvnProject = new MavenProject.Configuration().buildIn(checkoutDir)

        and:
        def develocityPluginConfig = new TcPluginConfig(
            develocityUrl: BaseExtensionApplicationTest.DEVELOCITY_URL,
            develocityExtensionVersion: BaseExtensionApplicationTest.DEVELOCITY_EXTENSION_VERSION,
            ccudExtensionVersion: BaseExtensionApplicationTest.CCUD_EXTENSION_VERSION,
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, develocityPluginConfig)

        then:
        1 * extensionApplicationListener.develocityExtensionApplied(BaseExtensionApplicationTest.DEVELOCITY_EXTENSION_VERSION)
        1 * extensionApplicationListener.ccudExtensionApplied(BaseExtensionApplicationTest.CCUD_EXTENSION_VERSION)

        and:
        outputContainsTeamCityServiceMessageBuildStarted(output)
        outputContainsTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << BaseExtensionApplicationTest.SUPPORTED_MAVEN_VERSIONS
    }

    def "applies CCUD extension via classpath when not defined in project where Develocity extension defined in project (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue BaseExtensionApplicationTest.DEVELOCITY_URL != null

        given:
        def mvnProject = new MavenProject.Configuration(
            develocityUrl: BaseExtensionApplicationTest.DEVELOCITY_URL,
            develocityExtensionVersion: BaseExtensionApplicationTest.DEVELOCITY_EXTENSION_VERSION,
        ).buildIn(checkoutDir)

        and:
        def develocityPluginConfig = new TcPluginConfig(
            develocityUrl: BaseExtensionApplicationTest.DEVELOCITY_URL,
            develocityExtensionVersion: BaseExtensionApplicationTest.DEVELOCITY_EXTENSION_VERSION,
            ccudExtensionVersion: BaseExtensionApplicationTest.CCUD_EXTENSION_VERSION,
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, develocityPluginConfig)

        then:
        0 * extensionApplicationListener.develocityExtensionApplied(_)
        1 * extensionApplicationListener.ccudExtensionApplied(BaseExtensionApplicationTest.CCUD_EXTENSION_VERSION)

        and:
        outputContainsTeamCityServiceMessageBuildStarted(output)
        outputContainsTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << BaseExtensionApplicationTest.SUPPORTED_MAVEN_VERSIONS
    }

    def "applies CCUD extension via project when defined in project (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue BaseExtensionApplicationTest.DEVELOCITY_URL != null

        given:
        def mvnProject = new MavenProject.Configuration(
            develocityUrl: BaseExtensionApplicationTest.DEVELOCITY_URL,
            develocityExtensionVersion: BaseExtensionApplicationTest.DEVELOCITY_EXTENSION_VERSION,
            ccudExtensionVersion: BaseExtensionApplicationTest.CCUD_EXTENSION_VERSION
        ).buildIn(checkoutDir)

        and:
        def develocityPluginConfig = new TcPluginConfig(
            develocityUrl: BaseExtensionApplicationTest.DEVELOCITY_URL,
            develocityExtensionVersion: BaseExtensionApplicationTest.DEVELOCITY_EXTENSION_VERSION,
            ccudExtensionVersion: BaseExtensionApplicationTest.CCUD_EXTENSION_VERSION,
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
        jdkCompatibleMavenVersion << BaseExtensionApplicationTest.SUPPORTED_MAVEN_VERSIONS
    }
}
