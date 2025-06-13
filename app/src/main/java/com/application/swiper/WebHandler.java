package com.application.swiper;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class WebHandler {
    private static OkHttpClient client;
    protected static String urlString = "http://localhost:8000"; // TODO: when testing, replace this and the one in network_securityconfig.xml with the right address

    public static OkHttpClient init() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .cookieJar(new SessionCookieJar())
                    .build();
        }
        return client;
    }
}

class SessionCookieJar implements CookieJar {
    private final List<Cookie> cookieStore = new ArrayList<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.clear();
        cookieStore.addAll(cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return cookieStore;
    }
}
