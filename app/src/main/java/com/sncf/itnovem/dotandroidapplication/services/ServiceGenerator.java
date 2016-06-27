package com.sncf.itnovem.dotandroidapplication.services;

import android.util.Base64;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



/**
 * Created by Journaud Nicolas on 29/07/15.
 */
public class ServiceGenerator {

    private static final String TAG = "ServiceGenerator";
    private ServiceGenerator() {
    }

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        return createService(serviceClass, baseUrl, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl, String email, String password) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(logging);

        if (email != null && password != null) {
            final String credentials = email + ":" + password;

            final String stringBasic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", stringBasic)
                            .header("language", "fr")
                            .header("Content-Type", "application/json")
                            .header("charset", "application/json")
                            .method(original.method(), original.body())
                            .build();

                    Response response = chain.proceed(request);

                    return response;
                }
            });
        } else {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("language", "fr")
                            .header("Content-Type", "application/json")
                            .header("charset", "application/json")
                            .method(original.method(), original.body())
                            .build();

                    Response response = chain.proceed(request);
                    return response;
                }
            });
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(serviceClass);
    }
}
