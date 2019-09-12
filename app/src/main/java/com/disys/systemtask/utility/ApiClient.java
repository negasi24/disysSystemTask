package com.disys.systemtask.utility;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ApiClient {
    private static OkHttpClient client = null;

    public static OkHttpClient getClient() {
        if (client == null) {
            createClient();
        }
        return client;
    }

    private static void createClient() {
        client = new OkHttpClient.Builder()
                .readTimeout(240, TimeUnit.SECONDS)
                .connectTimeout(240, TimeUnit.SECONDS)
                .addInterceptor(new NetworkInterceptor())
                .build();

    }
}
