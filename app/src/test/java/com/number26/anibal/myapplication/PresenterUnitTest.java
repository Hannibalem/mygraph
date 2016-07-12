package com.number26.anibal.myapplication;

import com.number26.anibal.myapplication.model.Interval;
import com.number26.anibal.myapplication.show_graph.MainActivity;
import com.number26.anibal.myapplication.show_graph.ShowGraphContract;
import com.number26.anibal.myapplication.show_graph.ShowGraphPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by anibal on 01.07.16.
 */
public class PresenterUnitTest {

    @Mock
    private ShowGraphContract.View mView;

    @Mock
    private ShowGraphContract.Repository mRepository;

    @Mock
    private MainActivity mActivity;

    ShowGraphPresenter mPresenter;

    @Before
    public void setupMyArticlesPresenter() {

        MockitoAnnotations.initMocks(this);

        mPresenter = new ShowGraphPresenter(mRepository,
                Schedulers.immediate(), Schedulers.immediate());
        mPresenter.attachView(mView);
    }

    @Test
    public void showGraphSuccess() {

        List<Interval> intervalList = getFakeIntervals();
        when(mRepository.fetchGraphData()).thenReturn(Observable.just(intervalList));

        mPresenter.showGraph();

        verify(mRepository).fetchGraphData();
        verify(mView).showGraph(intervalList);
        verify(mView, never()).showError();
    }

    @Test
    public void showGraphFailure() {

        when(mRepository.fetchGraphData()).thenReturn(Observable.error(new IOException()));

        mPresenter.showGraph();

        verify(mRepository).fetchGraphData();
        verify(mView, never()).showGraph(anyList());
        verify(mView).showError();
    }

    private List<Interval> getFakeIntervals() {

        List<Interval> intervalList = new ArrayList<>();
        Interval interval = new Interval();
        interval.x = 10;
        interval.y = 20;
        intervalList.add(interval);

        interval = new Interval();
        interval.x = 30;
        interval.y = 40;
        intervalList.add(interval);

        return intervalList;
    }
}
