package com.application.swiper;

import okhttp3.OkHttpClient;

public class WebHandler {
    private static OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }
}
