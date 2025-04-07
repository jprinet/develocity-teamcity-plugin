package com.gradle.develocity.teamcity;

import jetbrains.buildServer.serverSide.SBuild;
import org.jetbrains.annotations.NotNull;

public interface BuildScanDisplayArbiter {

    boolean showBuildScanInfo(@NotNull SBuild build);

}
