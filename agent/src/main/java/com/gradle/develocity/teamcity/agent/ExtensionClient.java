package com.gradle.develocity.teamcity.agent;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

final class ExtensionClient {

    private final OkHttpClient httpClient;

    ExtensionClient() {
        this.httpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new RetryInterceptor(2))
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    static class RetryInterceptor implements Interceptor {

        private final int maxRetries;

        public RetryInterceptor(int maxRetries) {
            this.maxRetries = maxRetries;
        }

        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String lastErrorMessage = "Unknown error";
            int attempt = 0;

            // Attempt the request up to maxRetries times
            while (attempt < maxRetries) {
                try {
                    Response response = chain.proceed(request);
                    if (response.isSuccessful()) {
                        return response;
                    } else {
                        // if unsuccessful, close the response to allow another attempt
                        response.close();
                        lastErrorMessage = String.format("Could not download the extension from %s (error code: %s)",request.url(), response.code());
                    }
                } catch (IOException e) {
                    // Keep trying until maxRetries
                } finally {
                    attempt++;
                }

                // Wait before retrying the request (optional delay)
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(2000); // 2s delay between retries
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            // max attempts reached
            throw new IOException(String.format("Maximum retry attempts exhausted - last error: %s", lastErrorMessage));
        }

    }

    void downloadExtension(
            Logger logger,
            Repository repository,
            String downloadUrl,
            File targetLocation,
            boolean overwrite
    ) throws IOException {
        if(!overwrite) {
            if(targetLocation.exists()) {
                logger.message("Extension already downloaded at " + targetLocation.getAbsolutePath() + ". Skipping download.");
                return;
            }
        }

        logger.message("Downloading extension from " + downloadUrl + " to " + targetLocation.getAbsolutePath());
        Request.Builder requestBuilder = new Request.Builder().url(downloadUrl);
        if (repository.getUsername() != null && repository.getPassword() != null) {
            String basicCredentials = Credentials.basic(repository.getUsername(), repository.getPassword());
            requestBuilder.addHeader("Authorization", basicCredentials);
        }

        try (Response response = httpClient.newCall(requestBuilder.build()).execute()) {
            try(ResponseBody responseBody = response.body()) {
                if(responseBody != null) {
                    try (FileOutputStream fos = new FileOutputStream(targetLocation); BufferedInputStream bufferedInputStream = new BufferedInputStream(responseBody.byteStream())) {
                        byte[] buffer = new byte[4096];
                        int n;
                        while (-1 != (n = bufferedInputStream.read(buffer))) {
                            fos.write(buffer, 0, n);
                        }
                    }
                }
            }
        }
    }
}
