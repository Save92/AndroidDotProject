package com.sncf.itnovem.dotandroidapplication.services;

import android.util.Base64;

import com.google.gson.Gson;

import java.io.IOException;

import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static retrofit2.Retrofit.*;


/**
 * Created by Save92 on 29/07/15.
 */
public class ServiceGenerator {

    private static final String TAG = "ServiceGenerator";
    // No need to instantiate this class.
    private ServiceGenerator() {
    }

//    public static Retrofit.Builder retrofit() {
//        return   new Retrofit.Builder()
//                .baseUrl(DotService.RESTAPIURL)
//                .addConverterFactory(GsonConverterFactory.create());
//
//    }


    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        // call basic auth generator method without user and pass
        return createService(serviceClass, baseUrl, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl, String email, String password) {
        // set endpoint url and use OkHTTP as HTTP client
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

// add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        if (email != null && password != null) {
            // concatenate username and password with colon for authentication
            final String credentials = email + ":" + password;
            //Log.v(TAG, "username :" + username+ ", pass :" + password);

            final String stringBasic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    // Customize the request
                    Request request = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", stringBasic)
                            .header("language", "fr")
                            .header("Content-Type", "application/json")
                            .header("charset", "application/json")
                            .method(original.method(), original.body())
                            .build();

                    Response response = chain.proceed(request);

                    // Customize or return the response
                    return response;
                }
            });
        } else {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    // Customize the request
                    Request request = original.newBuilder()
                            .header("Accept", "application/json")
//                            .header("Authorization", CurrentUser.getToken())
                            .header("language", "fr")
                            .header("Content-Type", "application/json")
                            .header("charset", "application/json")
                            .method(original.method(), original.body())
                            .build();

                    Response response = chain.proceed(request);
                    // Customize or return the response
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
        //return retrofit;
    }
}
