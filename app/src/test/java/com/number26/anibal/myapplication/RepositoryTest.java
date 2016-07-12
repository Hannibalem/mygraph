package com.number26.anibal.myapplication;

import com.number26.anibal.myapplication.api.BaseResponse;
import com.number26.anibal.myapplication.model.Interval;
import com.number26.anibal.myapplication.service.Number26Service;
import com.number26.anibal.myapplication.show_graph.ShowGraphContract;
import com.number26.anibal.myapplication.show_graph.ShowGraphRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.observers.TestSubscriber;

/**
 * Created by anibal on 11/08/16.
 */
public class RepositoryTest {

    @Mock
    Number26Service mService;

    private ShowGraphContract.Repository mRepository;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        mRepository = new ShowGraphRepository(mService);
    }

    @Test
    public void getGraphSuccess() {

        //Given
        Mockito.when(mService.getGraph(Matchers.anyString()))
                .thenReturn(Observable.just(getFakeIntervals()));

        //When
        TestSubscriber<List<Interval>> subscriber = new TestSubscriber<>();
        mRepository.fetchGraphDataWithRetry().subscribe(subscriber);

        //Then
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        List<List<Interval>> onNextEvents = subscriber.getOnNextEvents();
        List<Interval> intervals = onNextEvents.get(0);
        Assert.assertEquals(10, intervals.get(0).x);
        Assert.assertEquals(30, intervals.get(1).x);
        Mockito.verify(mService).getGraph("json");
    }

    @Test
    public void getGraphHttpFailure() {

        //Given
        HttpException exception = getHttpExceptionError();
        Mockito.when(mService.getGraph(Matchers.anyString()))
                .thenReturn((Observable) Observable.error(exception));

        //When
        TestSubscriber<List<Interval>> subscriber = new TestSubscriber<>();
        mRepository.fetchGraphDataWithRetry().subscribe(subscriber);

        //Then
        subscriber.awaitTerminalEvent();
        subscriber.assertError(HttpException.class);

        List<Throwable> onErrorEvents = subscriber.getOnErrorEvents();
        Throwable error = onErrorEvents.get(0);
        Assert.assertEquals(error, exception);
        Mockito.verify(mService).getGraph("json");
    }

    private HttpException getHttpExceptionError() {

        return new HttpException(Response.error(403,
                ResponseBody.create(MediaType.parse("application/json"), "Forbidden")));
    }

    @Test
    public void getGraphIOFailure() {

        //Given
        IOException exception = getIOExceptionError();
        Mockito.when(mService.getGraph(Matchers.anyString()))
                .thenReturn((Observable) Observable.error(exception),
                        Observable.just(getFakeIntervals()));

        //When
        TestSubscriber<List<Interval>> subscriber = new TestSubscriber<>();
        mRepository.fetchGraphDataWithRetry().subscribe(subscriber);

        //Then
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        Mockito.verify(mService, Mockito.times(2)).getGraph("json");

        List<List<Interval>> onNextEvents = subscriber.getOnNextEvents();
        List<Interval> intervals = onNextEvents.get(0);
        Assert.assertEquals(10, intervals.get(0).x);
        Assert.assertEquals(30, intervals.get(1).x);
    }

    private IOException getIOExceptionError() {

        return new IOException();
    }

    private BaseResponse getFakeIntervals() {

        List<Interval> intervalList = new ArrayList<>();
        Interval interval = new Interval();
        interval.x = 10;
        intervalList.add(interval);

        interval = new Interval();
        interval.x = 30;
        intervalList.add(interval);

        BaseResponse response = new BaseResponse();
        response.values = intervalList;

        return response;
    }
}
