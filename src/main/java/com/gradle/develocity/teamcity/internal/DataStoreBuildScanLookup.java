package com.gradle.develocity.teamcity.internal;

import jetbrains.buildServer.serverSide.SBuild;
import com.gradle.develocity.teamcity.BuildScanDataStore;
import com.gradle.develocity.teamcity.BuildScanLookup;
import com.gradle.develocity.teamcity.BuildScanReference;
import com.gradle.develocity.teamcity.BuildScanReferences;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class DataStoreBuildScanLookup implements BuildScanLookup {

    private final BuildScanLookup delegate;
    private final BuildScanDataStore buildScanDataStore;

    public DataStoreBuildScanLookup(
        @NotNull BuildScanDataStore buildScanDataStore,
        @NotNull BuildScanLookup delegate
    ) {
        this.delegate = delegate;
        this.buildScanDataStore = buildScanDataStore;
    }

    @Override
    @NotNull
    public BuildScanReferences getBuildScansForBuild(@NotNull SBuild build) {
        List<BuildScanReference> buildScanReferences = buildScanDataStore.fetch(build);
        return buildScanReferences == null ? delegate.getBuildScansForBuild(build) : BuildScanReferences.of(buildScanReferences);
    }

}
