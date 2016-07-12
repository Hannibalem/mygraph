package com.number26.anibal.myapplication.dagger.module;

import com.number26.anibal.myapplication.service.Number26Service;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anibal on 01.07.16.
 */
@Module
public class ServiceModule {

    @Provides
    @Singleton // needs to be consistent with the component scope
    public Number26Service provideRetrofitService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://blockchain.info/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(Number26Service.class);
    }
}
