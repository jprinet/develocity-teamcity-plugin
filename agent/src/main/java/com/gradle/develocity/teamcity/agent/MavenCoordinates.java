package com.gradle.develocity.teamcity.agent;

/**
 * Describes a set of Maven coordinates, represented as a GAV.
 */
final class MavenCoordinates {

    private final String groupId;

    private final String artifactId;

    private final String version;

    MavenCoordinates(String groupId, String artifactId) {
        this(groupId, artifactId, "[RELEASE]");
    }

    MavenCoordinates(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    String getGroupId() {
        return groupId;
    }

    String getArtifactId() {
        return artifactId;
    }

    String getVersion() {
        return version;
    }

    String getJarName() {
        return artifactId + "-" + version + ".jar";
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%s", groupId, artifactId, version);
    }

}
