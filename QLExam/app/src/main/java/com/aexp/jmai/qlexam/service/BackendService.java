package com.aexp.jmai.qlexam.service;

import com.squareup.okhttp.OkHttpClient;
import java.util.concurrent.TimeUnit;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class BackendService {
    private static final String API_URL = "http://www.giantbomb.com/api";

    private BackendService() {

    }

    public static QueryClient setupQueryClient() {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);

        RestAdapter restAdapter;
        restAdapter = new RestAdapter.Builder()
                          .setEndpoint(API_URL)
                          .setClient(new OkClient(okHttpClient))
                          .build();
        return restAdapter.create(QueryClient.class);
    }
}
