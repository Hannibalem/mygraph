package com.number26.anibal.myapplication.show_graph;

import com.number26.anibal.myapplication.model.Interval;

import java.util.List;

import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by anibal on 01.07.16.
 */
public class ShowGraphPresenter implements ShowGraphContract.UserActionListener {

    private final Scheduler mMainScheduler, mIoScheduler;

    private ShowGraphContract.View mView;

    private ShowGraphContract.Repository mRepository;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public ShowGraphPresenter(ShowGraphContract.Repository repository, Scheduler ioScheduler, Scheduler mainScheduler) {
        mRepository = repository;
        mMainScheduler = mainScheduler;
        mIoScheduler = ioScheduler;
    }

    @Override
    public void attachView(ShowGraphContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mCompositeSubscription.clear();
        mView = null;
    }

    public ShowGraphContract.View getView() {
        return mView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) {
            throw new MvpViewNotAttachedException();
        }
    }

    private boolean isViewAttached() {
        return mView != null;
    }

    protected void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    protected static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" + " requesting data to the Presenter");
        }
    }

    @Override
    public void showGraph() {
        checkViewAttached();
        addSubscription(mRepository.fetchGraphDataWithRetry()
                .subscribeOn(mIoScheduler)
                .observeOn(mMainScheduler)
                .subscribe(new Subscriber<List<Interval>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showError();
                    }

                    @Override
                    public void onNext(List<Interval> intervals) {
                        getView().showGraph(intervals);
                    }
                }));
    }

}
