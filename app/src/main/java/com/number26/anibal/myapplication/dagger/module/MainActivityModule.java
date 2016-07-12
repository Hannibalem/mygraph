package com.number26.anibal.myapplication.dagger.module;

import android.content.Context;

import com.number26.anibal.myapplication.dagger.scope.ActivityScope;
import com.number26.anibal.myapplication.service.Number26Service;
import com.number26.anibal.myapplication.show_graph.MainActivity;
import com.number26.anibal.myapplication.show_graph.ShowGraphContract;
import com.number26.anibal.myapplication.show_graph.ShowGraphPresenter;
import com.number26.anibal.myapplication.show_graph.ShowGraphRepository;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by anibal on 01.07.16.
 */
@Module
public class MainActivityModule {

    private MainActivity mActivity;

    public MainActivityModule(MainActivity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityScope
    Context providesContext() {
        return mActivity.getApplicationContext();
    }

    @Provides
    @ActivityScope
    ShowGraphContract.Repository providesRepository(Number26Service service) {
        return new ShowGraphRepository(service);
    }

    @Provides
    @ActivityScope
    @Named("ioScheduler")
    Scheduler providesIoScheduler() {
        return Schedulers.io();
    }

    @Provides
    @ActivityScope
    @Named("mainScheduler")
    Scheduler providesMainScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @ActivityScope
    ShowGraphContract.UserActionListener providesPresenter(ShowGraphContract.Repository repository,
                                                           @Named("ioScheduler") Scheduler ioScheduler,
                                                           @Named("mainScheduler") Scheduler mainScheduler) {
        return new ShowGraphPresenter(repository, ioScheduler, mainScheduler);
    }
}
