package com.cabot.androidtemplateproject.apimanager;


import com.cabot.androidtemplateproject.constants.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {

    private static RetrofitCommonService RETROFIT_CLIENT;

    public RetrofitClient() {
    }

    public static RetrofitCommonService getInstance() {

        RETROFIT_CLIENT = null;
/*
        if REST_CLIENT is null then set-up again.
*/
        if (RETROFIT_CLIENT == null) {
            setupRestClient();
        }
        return RETROFIT_CLIENT;
    }

    private static void setupRestClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(60000, TimeUnit.SECONDS);
        httpClient.readTimeout(120000, TimeUnit.SECONDS);
        httpClient.retryOnConnectionFailure(true);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder requestBuilder = request.newBuilder()
                        .header(Constants.DEVICE_ID_KEY, "params")
                        .header(Constants.DEVICE_TYPE_KEY, "params")
                        .header(Constants.USER_ID_KEY, "params")
                        .header(Constants.LANGUAGE_KEY, "params");

                Request modifiedRequest = requestBuilder.build();
                return chain.proceed(modifiedRequest);
            }
        });

        httpClient.addNetworkInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        RETROFIT_CLIENT = retrofit.create(RetrofitCommonService.class);
    }
}
