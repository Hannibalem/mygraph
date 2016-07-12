package com.number26.anibal.myapplication.show_graph;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.number26.anibal.myapplication.api.BaseResponse;
import com.number26.anibal.myapplication.model.Interval;
import com.number26.anibal.myapplication.service.Number26Service;

import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by anibal on 01.07.16.
 */
public class ShowGraphRepository implements ShowGraphContract.Repository {

    private Number26Service mService;

    public ShowGraphRepository(Number26Service service) {
        mService = service;
    }

    @Override
    public Observable<List<Interval>> fetchGraphData() {
        return Observable.defer(() -> mService.getGraph("json"))
                .onErrorResumeNext(throwable -> {
                    throwable.printStackTrace();
                    return Observable.error(throwable);
                }).map(baseResponse -> baseResponse.values);
    }

    @Override
    public Observable<List<Interval>> fetchGraphDataWithRetry() {
        return Observable.defer(() -> mService.getGraph("json"))
                .retryWhen(new RetryFunc())
                .map(baseResponse -> baseResponse.values);
    }

    private static class RetryFunc implements Func1<Observable<? extends Throwable>, Observable<?>> {

        private int mRetries;

        @Override
        public Observable<?> call(Observable<? extends Throwable> observable) {
            return observable.flatMap(new Func1<Throwable, Observable<?>>() {
                @Override
                public Observable<?> call(Throwable throwable) {
                    if (mRetries < 2 && throwable instanceof IOException) {
                        throwable.printStackTrace();
                        ++mRetries;
                        return Observable.just(null);
                    }

                    return Observable.error(throwable);
                }
            });
        }
    }
}
