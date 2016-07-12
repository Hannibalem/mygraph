package com.number26.anibal.myapplication.dagger.component;

import com.number26.anibal.myapplication.dagger.module.AppModule;
import com.number26.anibal.myapplication.dagger.module.MainActivityModule;
import com.number26.anibal.myapplication.dagger.module.ServiceModule;
import com.number26.anibal.myapplication.show_graph.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by anibal on 01.07.16.
 */
@Singleton
@Component(modules = {ServiceModule.class, AppModule.class})
public interface AppComponent {

    MainActivityComponent plus(MainActivityModule mainActivityModule);

}
