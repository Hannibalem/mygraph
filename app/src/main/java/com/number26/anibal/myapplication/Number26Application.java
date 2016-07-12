package com.number26.anibal.myapplication;

import android.app.Application;

import com.number26.anibal.myapplication.dagger.component.AppComponent;
import com.number26.anibal.myapplication.dagger.component.DaggerAppComponent;
import com.number26.anibal.myapplication.dagger.module.AppModule;
import com.number26.anibal.myapplication.dagger.module.ServiceModule;

/**
 * Created by anibal on 30.06.16.
 */
public class Number26Application extends Application {

    public AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this)).serviceModule(new ServiceModule()).build();
    }
}
