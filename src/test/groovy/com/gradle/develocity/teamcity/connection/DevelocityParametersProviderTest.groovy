package com.gradle.develocity.teamcity.connection

import com.gradle.develocity.teamcity.token.DevelocityAccessCredentials
import com.gradle.develocity.teamcity.token.ShortLivedTokenClient
import com.gradle.develocity.teamcity.token.ShortLivedTokenClientFactory
import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.SBuildType
import jetbrains.buildServer.serverSide.SProject
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor
import jetbrains.buildServer.serverSide.oauth.OAuthConstants
import jetbrains.buildServer.serverSide.parameters.BuildParametersProvider
import spock.lang.Specification
import spock.lang.Unroll

import static DevelocityConnectionConstants.*

@Unroll
class DevelocityParametersProviderTest extends Specification {

    BuildParametersProvider buildParametersProvider

    Map<String, String> descriptorParams
    Map<String, String> higherDescriptorParams

    SBuild sBuild
    SBuildType sBuildType
    SProject sProject
    SProjectFeatureDescriptor sProjectFeatureDescriptor
    SProjectFeatureDescriptor higherProjectFeatureDescriptor

    void setup() {
        def shortLivedTokenClient = Mock(ShortLivedTokenClient) {
            get(_, _, _) >> { arguments -> Optional.of(DevelocityAccessCredentials.HostnameAccessKey.of(arguments[1].hostname, '<token>')) }
        }
        def shortLivedTokenClientFactory = Mock(ShortLivedTokenClientFactory) {
            create(_) >> shortLivedTokenClient
        }
        buildParametersProvider = new DevelocityParametersProvider(shortLivedTokenClientFactory)

        descriptorParams = [(OAuthConstants.OAUTH_TYPE_PARAM): DEVELOCITY_CONNECTION_PROVIDER]
        higherDescriptorParams = [(OAuthConstants.OAUTH_TYPE_PARAM): DEVELOCITY_CONNECTION_PROVIDER]

        sBuild = Stub()
        sBuildType = Stub()
        sProject = Stub()
        sProjectFeatureDescriptor = Stub()
        higherProjectFeatureDescriptor = Stub()

        sBuild.getBuildType() >> sBuildType
        sBuildType.getProject() >> sProject
        sProject.getAvailableFeaturesOfType(OAuthConstants.FEATURE_TYPE) >> [sProjectFeatureDescriptor, higherProjectFeatureDescriptor]
        sProjectFeatureDescriptor.getParameters() >> descriptorParams
        higherProjectFeatureDescriptor.getParameters() >> higherDescriptorParams
    }

    def "returns no elements when no buildType is defined"() {
        given:
        sBuild.getBuildType() >> null

        when:
        def parameters = buildParametersProvider.getParameters(sBuild, false)

        then:
        parameters.isEmpty()
    }

    def "returns no elements when no OAuth providers found"() {
        given:
        sProject.getAvailableFeaturesOfType(OAuthConstants.FEATURE_TYPE) >> []

        when:
        def parameters = buildParametersProvider.getParameters(sBuild, false)

        then:
        parameters.isEmpty()
    }

    def "returns no elements when no matching OAuth provider found"() {
        given:
        descriptorParams[OAuthConstants.OAUTH_TYPE_PARAM] = "other"

        when:
        def parameters = buildParametersProvider.getParameters(sBuild, false)

        then:
        parameters.isEmpty()
    }

    def "sets #configParam config param when #descriptorParam descriptor param is set"() {
        given:
        descriptorParams[descriptorParam] = value

        when:
        def parameters = buildParametersProvider.getParameters(sBuild, false)

        then:
        parameters.get(configParam) == value

        where:
        descriptorParam                         | configParam                                          | value
        GRADLE_PLUGIN_REPOSITORY_URL            | GRADLE_PLUGIN_REPOSITORY_URL_CONFIG_PARAM            | 'https://plugins.example.com'
        DEVELOCITY_URL                          | DEVELOCITY_URL_CONFIG_PARAM                          | 'https://develocity.example.com'
        ALLOW_UNTRUSTED_SERVER                  | ALLOW_UNTRUSTED_SERVER_CONFIG_PARAM                  | 'true'
        ENFORCE_DEVELOCITY_URL                  | ENFORCE_DEVELOCITY_URL_CONFIG_PARAM                  | 'true'
        DEVELOCITY_PLUGIN_VERSION               | DEVELOCITY_PLUGIN_VERSION_CONFIG_PARAM               | '1.0.0'
        CCUD_PLUGIN_VERSION                     | CCUD_PLUGIN_VERSION_CONFIG_PARAM                     | '1.0.0'
        DEVELOCITY_EXTENSION_VERSION            | DEVELOCITY_EXTENSION_VERSION_CONFIG_PARAM            | '1.0.0'
        CCUD_EXTENSION_VERSION                  | CCUD_EXTENSION_VERSION_CONFIG_PARAM                  | '1.0.0'
        CUSTOM_DEVELOCITY_EXTENSION_COORDINATES | CUSTOM_DEVELOCITY_EXTENSION_COORDINATES_CONFIG_PARAM | '1.0.0'
        CUSTOM_CCUD_EXTENSION_COORDINATES       | CUSTOM_CCUD_EXTENSION_COORDINATES_CONFIG_PARAM       | '1.0.0'
        INSTRUMENT_COMMAND_LINE_BUILD_STEP      | INSTRUMENT_COMMAND_LINE_BUILD_STEP_CONFIG_PARAM      | 'true'
    }

    def "sets access key config param with short lived token"() {
        given:
        descriptorParams[descriptorParam] = value

        when:
        def parameters = buildParametersProvider.getParameters(sBuild, false)

        then:
        parameters.get(configParam) == expected

        where:
        descriptorParam       | configParam                          | value                                                     | expected
        DEVELOCITY_ACCESS_KEY | DEVELOCITY_ACCESS_KEY_ENV_VAR        | 'develocity.example.com=xxx'                              | 'develocity.example.com=<token>'
        DEVELOCITY_ACCESS_KEY | GRADLE_ENTERPRISE_ACCESS_KEY_ENV_VAR | 'develocity.example.com=xxx'                              | 'develocity.example.com=<token>'
        DEVELOCITY_ACCESS_KEY | DEVELOCITY_ACCESS_KEY_ENV_VAR        | 'xxx'                                                     | null
        DEVELOCITY_ACCESS_KEY | DEVELOCITY_ACCESS_KEY_ENV_VAR        | 'develocity1.example.com=xxx;develocity2.example.com=xxx' | 'develocity1.example.com=<token>;develocity2.example.com=<token>'
    }

    def "gets configuration from first descriptor"() {
        given:
        def value = 'https://develocity.example.com'
        descriptorParams[DEVELOCITY_URL] = value
        higherDescriptorParams[DEVELOCITY_URL] = 'https://develocity.example.invalid'

        when:
        def parameters = buildParametersProvider.getParameters(sBuild, false)

        then:
        parameters.get(DEVELOCITY_URL_CONFIG_PARAM) == value
    }

    def "inherits configuration parameter from last descriptor when not set in first descriptor"() {
        given:
        def value = 'https://develocity.example.com'
        higherDescriptorParams[DEVELOCITY_URL] = value

        when:
        def parameters = buildParametersProvider.getParameters(sBuild, false)

        then:
        parameters.get(DEVELOCITY_URL_CONFIG_PARAM) == value
    }

    def "inherits configuration parameter from last descriptor when set to null in first descriptor"() {
        given:
        def value = 'https://develocity.example.com'
        descriptorParams[DEVELOCITY_URL] = null
        higherDescriptorParams[DEVELOCITY_URL] = value

        when:
        def parameters = buildParametersProvider.getParameters(sBuild, false)

        then:
        parameters.get(DEVELOCITY_URL_CONFIG_PARAM) == value
    }

}
