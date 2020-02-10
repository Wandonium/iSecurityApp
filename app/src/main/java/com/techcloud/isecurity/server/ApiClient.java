package com.techcloud.isecurity.server;

import android.content.Context;
import android.util.Log;

import com.techcloud.isecurity.helpers.InputException;
import com.techcloud.isecurity.helpers.ServerException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String TAG = ApiClient.class.getSimpleName();

    private static Retrofit retrofit = null;
    private static int REQUEST_TIMEOUT = 90;
    private static OkHttpClient okHttpClient;

    public static Retrofit getClient(Context context) {
        if(okHttpClient == null) {
            initOkHttp(context);
        }

        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://isecurity-server.herokuapp.com/api/v1/")
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static void initOkHttp(final Context context) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .cache(null);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                okhttp3.Response response = chain.proceed(request);
                String headers = response.headers().get("Content-Type");
                Log.d(TAG, "Headers: " + headers);

                if(headers.equals("text/html; charset=UTF-8")){
                    throw new InputException("Fatal Error! No image file chosen!");
                } else if(headers.equals("application/x-www-form-urlencoded")){
                    throw new ServerException("Fatal Error! Server Error!");
                } else if (headers.equals("application/json")) {
                    return response;
                }
                return response;
            }
        });
        okHttpClient = httpClient.build();
    }
}
