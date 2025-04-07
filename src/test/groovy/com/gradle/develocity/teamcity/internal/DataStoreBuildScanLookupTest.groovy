package com.gradle.develocity.teamcity.internal

import jetbrains.buildServer.serverSide.SBuild
import com.gradle.develocity.teamcity.BuildScanDataStore
import com.gradle.develocity.teamcity.BuildScanLookup
import com.gradle.develocity.teamcity.BuildScanReference
import com.gradle.develocity.teamcity.BuildScanReferences
import spock.lang.Specification

class DataStoreBuildScanLookupTest extends Specification {

    def "delegates lookup when no data is found in store"() {
        given:
        def store = Mock(BuildScanDataStore)
        def delegate = Mock(BuildScanLookup)
        def lookup = new DataStoreBuildScanLookup(store, delegate)

        def sbuild = Stub(SBuild)

        when:
        lookup.getBuildScansForBuild(sbuild)

        then:
        1 * delegate.getBuildScansForBuild(_) >> BuildScanReferences.of()
    }

    def "returns store results"() {
        given:
        def store = Mock(BuildScanDataStore)
        def delegate = Mock(BuildScanLookup)
        def lookup = new DataStoreBuildScanLookup(store, delegate)

        def sbuild = Stub(SBuild)
        def reference = new BuildScanReference("someid", "https://gradle.company.com/s/someid")
        store.fetch(sbuild) >> Collections.singletonList(reference)

        when:
        def result = lookup.getBuildScansForBuild(sbuild)

        then:
        result == BuildScanReferences.of(reference)
        0 * delegate.getBuildScansForBuild(_)
    }

}
