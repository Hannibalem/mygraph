package com.number26.anibal.myapplication.dagger.component;

import com.number26.anibal.myapplication.dagger.module.MainActivityModule;
import com.number26.anibal.myapplication.dagger.scope.ActivityScope;
import com.number26.anibal.myapplication.show_graph.MainActivity;
import com.number26.anibal.myapplication.show_graph.ShowGraphPresenter;

import dagger.Subcomponent;

/**
 * Created by anibal on 01.07.16.
 */
@ActivityScope
@Subcomponent(modules = {MainActivityModule.class})
public interface MainActivityComponent {

    void inject(MainActivity activity);
}
