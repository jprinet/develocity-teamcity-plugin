package com.gradle.develocity.teamcity.token;

public class ShortLivedTokenClientFactory {

    public ShortLivedTokenClient create(boolean allowUntrusted) {
        return new ShortLivedTokenClient(allowUntrusted);
    }

}
