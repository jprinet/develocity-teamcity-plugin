package com.gradle.develocity.teamcity.token;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ShortLivedTokenClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShortLivedTokenClient.class);
    private static final RequestBody EMPTY_BODY = RequestBody.create(new byte[]{});
    private static final int MAX_RETRIES = 3;
    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(1);

    private final OkHttpClient httpClient;

    public ShortLivedTokenClient(boolean allowUntrusted) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder().callTimeout(10, TimeUnit.SECONDS);
        if (allowUntrusted) {
            builder.hostnameVerifier((hostname, session) -> true);
            try {
                TrustManager[] allTrustingTrustManager = createAllTrustingTrustManager();
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, allTrustingTrustManager, null);

                builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) allTrustingTrustManager[0]);
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException(e);
            }
        }

        this.httpClient = builder.build();
    }

    public Optional<DevelocityAccessCredentials.HostnameAccessKey> get(String server, DevelocityAccessCredentials.HostnameAccessKey accessKey, @Nullable Integer expiry) {
        String url = normalize(server) + "api/auth/token";
        if (expiry != null) {
            url = url + "?expiresInHours=" + expiry;
        }

        Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + accessKey.getKey())
            .addHeader("Content-Type", "application/json")
            .post(EMPTY_BODY)
            .build();

        int tryCount = 0;
        Integer errorCode = null;
        while (tryCount < MAX_RETRIES) {
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.code() == 200 && response.body() != null) {
                    return Optional.of(DevelocityAccessCredentials.HostnameAccessKey.of(accessKey.getHostname(), response.body().string()));
                } else if (response.code() == 401) {
                    LOGGER.warn("Develocity short lived token request failed {} with status code 401", url);
                    return Optional.empty();
                } else {
                    tryCount++;
                    errorCode = response.code();
                    Thread.sleep(RETRY_INTERVAL.toMillis());
                }
            } catch (IOException e) {
                LOGGER.warn("Short lived token request failed {}", url, e);
                return Optional.empty();
            } catch (InterruptedException e) {
                // Ignore sleep exception
            }
        }
        LOGGER.warn("Develocity short lived token request failed {} with status code {}", url, errorCode);
        return Optional.empty();
    }

    private static String normalize(String server) {
        if (server.endsWith("/")) {
            return server;
        }

        return server + "/";
    }

    private static TrustManager[] createAllTrustingTrustManager() {
        return new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };
    }

}
