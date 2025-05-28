package com.gradle.develocity.teamcity.agent;

final class Repository {

    private final String url;
    private final String username;
    private final String password;

    Repository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    String getUrl() {
        return url;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }
}
