package com.myorg;

import com.gradle.develocity.agent.maven.api.DevelocityApi;
import com.gradle.develocity.agent.maven.api.DevelocityListener;
import com.gradle.develocity.agent.maven.api.cache.BuildCacheApi;
import com.gradle.develocity.agent.maven.api.scan.BuildScanApi;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.maven.execution.MavenSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConventionDevelocityListener implements DevelocityListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConventionDevelocityListener.class);

    @Override
    public void configure(DevelocityApi develocity, MavenSession session) {
        collectTeamCityData(develocity.getBuildScan());
    }

    private void collectTeamCityData(BuildScanApi buildScan) {
        String teamcityBuildPropertiesFile = System.getenv("TEAMCITY_BUILD_PROPERTIES_FILE");
        if (teamcityBuildPropertiesFile != null) {
            Properties buildProperties = readPropertiesFile(teamcityBuildPropertiesFile);
            String teamCityBuildId = buildProperties.getProperty("teamcity.build.id");
            if (teamCityBuildId != null && !teamCityBuildId.isEmpty()) {
                String teamcityConfigFile = buildProperties.getProperty("teamcity.configuration.properties.file");
                if (teamcityConfigFile != null && !teamcityConfigFile.isEmpty()) {
                    Properties configProperties = readPropertiesFile(teamcityConfigFile);
                    String vcsrootUrl = configProperties.getProperty("vcsroot.url");
                    if (vcsrootUrl != null) {
                        String[] paths = vcsrootUrl.split("/");
                        int idx = 1;
                        for(String path : paths) {
                            if(path != null && !path.isEmpty()) {
                                buildScan.value("vcsroot" + idx++, path);
                            }
                        }
                    }
                }
            }
        }
    }

    private Properties readPropertiesFile(String name) {
        try (InputStream input = new FileInputStream(name)) {
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
