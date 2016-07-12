package com.number26.anibal.myapplication.show_graph;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.number26.anibal.myapplication.Number26Application;
import com.number26.anibal.myapplication.R;
import com.number26.anibal.myapplication.custom.GraphView;
import com.number26.anibal.myapplication.dagger.module.MainActivityModule;
import com.number26.anibal.myapplication.model.Interval;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements ShowGraphContract.View {

    private List<Interval> mIntervals = new ArrayList<>();

    private View mLoader;

    private GraphView mGraph;

    @Inject
    ShowGraphContract.UserActionListener mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bindViews();

        ((Number26Application) getApplication()).mAppComponent
                .plus(new MainActivityModule(this)).inject(this);

        mPresenter.attachView(this);

        if (savedInstanceState != null) {
            showGraph(getLastCustomNonConfigurationInstance());
        } else {
            mPresenter.showGraph();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mIntervals;
    }

    @Override
    public List<Interval> getLastCustomNonConfigurationInstance() {
        return (List<Interval>) super.getLastCustomNonConfigurationInstance();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    private void bindViews() {
        mLoader = findViewById(R.id.loader);
        mGraph = (GraphView) findViewById(R.id.graph_view);
    }

    @Override
    public void showGraph(List<Interval> intervalList) {
        mIntervals = intervalList;
        mLoader.animate().alpha(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mLoader.setVisibility(View.GONE);
                mGraph.setListIntervals(mIntervals);
                mGraph.invalidate();
                mGraph.animate().alpha(1);
            }
        });
    }

    @Override
    public void showError() {
        mLoader.setVisibility(View.GONE);
        Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
    }
}
