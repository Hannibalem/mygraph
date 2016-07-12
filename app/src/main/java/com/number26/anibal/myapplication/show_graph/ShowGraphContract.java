package com.number26.anibal.myapplication.show_graph;

import com.number26.anibal.myapplication.model.Interval;

import java.util.List;

import rx.Observable;

/**
 * Created by anibal on 01.07.16.
 */
public interface ShowGraphContract {

    interface View {

        void showGraph(List<Interval> intervalList);

        void showError();
    }

    interface UserActionListener {

        void attachView(View view);

        void detachView();

        void showGraph();
    }

    interface Repository {

        Observable<List<Interval>> fetchGraphData();

        Observable<List<Interval>> fetchGraphDataWithRetry();
    }
}
