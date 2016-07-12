package com.number26.anibal.myapplication.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.number26.anibal.myapplication.R;
import com.number26.anibal.myapplication.model.Interval;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anibal on 30.06.16.
 */
public class GraphView extends ViewGroup {

    private final String TAG = getClass().getName();

    private final int VALUE_BAR_INTERVAL = 100;

    private final int VALUE_DATE_INTERVAL = 13;

    private final int VALUE_ROUNDED_VALUE = 100;

    private int mLineStrokeWidth = getResources().getDimensionPixelSize(R.dimen.graph_stroke);

    private int mValueTextSize = getResources().getDimensionPixelSize(R.dimen.value_text_size);

    private int mDateTextSize = getResources().getDimensionPixelSize(R.dimen.date_text_size);

    private List<Interval> mListIntervals = new ArrayList<>();

    private int mMaxValue;

    private long mMinDate;

    private int mSteps;

    private Paint mPaint;

    private Paint mPaintWords;

    private Path mPathGraph;

    private Path mPathBars;

    private DateTime mTime;

    private DateTimeFormatter mFormatter;

    private Rect mBounds;

    private Rect mTempRect;

    public GraphView(Context context) {
        super(context);
        init();
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mLineStrokeWidth);

        mPaintWords = new Paint();
        mPaintWords.setAntiAlias(true);
        mPaintWords.setColor(ContextCompat.getColor(getContext(), android.R.color.background_dark));
        mPaintWords.setStyle(Paint.Style.FILL);
        mPaintWords.setStrokeWidth(mLineStrokeWidth);

        mPathGraph = new Path();
        mPathBars = new Path();

        mBounds = new Rect();

        mTempRect = new Rect();
    }

    public void setListIntervals(List<Interval> listIntervals) {
        this.mListIntervals = listIntervals;
        getMaxMinValue();
        mTime = new DateTime(mMinDate);
        mFormatter = DateTimeFormat.forPattern("MMM/yy");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        View child = getChildAt(0);
        LayoutParams lp = child.getLayoutParams();

        int childWidth;
        if (lp.width == LayoutParams.WRAP_CONTENT) {
            childWidth = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),
                    MeasureSpec.AT_MOST);
        } else if (lp.width == LayoutParams.MATCH_PARENT) {
            childWidth = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),
                    MeasureSpec.EXACTLY);
        } else { //the width has been given in dps in the layout XML
            childWidth = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
        }

        int childHeight;
        if (lp.height == LayoutParams.WRAP_CONTENT) {
            childHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec),
                    MeasureSpec.AT_MOST);
        } else if (lp.height == LayoutParams.MATCH_PARENT) {
            childHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec),
                    MeasureSpec.EXACTLY);
        } else { //the height has been given in dps in the layout XML
            childHeight = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
        }

        getChildAt(0).measure(childWidth, childHeight);

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        getChildAt(0).layout(
                getWidth()/2 - getChildAt(0).getMeasuredWidth()/2,
                getHeight()/2,
                getWidth()/2 + getChildAt(0).getMeasuredWidth()/2,
                getHeight()/2 + getChildAt(0).getMeasuredHeight());
    }

    //Not really needed because the child is being shown completely,
    //but good to have it here as a future reference.
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final int save = canvas.save(Canvas.CLIP_SAVE_FLAG);

        /*
        //Get Rect of the whole canvas.
        canvas.getClipBounds(mTempRect);

        //Edit/Crop the Rect as you wish
        mTempRect.left = child.getLeft();
        mTempRect.top = child.getTop();
        mTempRect.right = child.getRight();
        mTempRect.bottom = child.getBottom();

        //Clip the canvas with the cropped Rect
        canvas.clipRect(mTempRect);
        */

        //Or just
        canvas.clipRect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());

        boolean result =  super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(save);
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mListIntervals.size() == 0) {
            return;
        }

        //BARS
        drawBars(canvas);

        //GRAPH
        drawGraph(canvas);

        if (mSteps < mListIntervals.size()) {
            invalidate();
        }
    }

    private void drawBars(Canvas canvas) {

        mPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));

        int topLevel = mMaxValue / VALUE_BAR_INTERVAL;

        float spaceBetweenBars = (getHeight() - getPaddingStart() * 2) / topLevel;

        float spaceBetweenDates = (float) (getWidth() - getPaddingStart()) / VALUE_DATE_INTERVAL;

        for (int i = topLevel; i >= 0; i--) {

            //Add new bar to path

            if(mSteps == 0) {

                mPathBars.moveTo(getPaddingStart(),
                        (i * spaceBetweenBars) + getPaddingStart());
                mPathBars.lineTo(getWidth() - getPaddingStart(),
                        (i * spaceBetweenBars) + getPaddingStart());
            }

            if (i == topLevel) {
                continue;
            }

            //Draw dates under bottom bar

            if (i == 0) {

                mPaintWords.setTextSize(mDateTextSize);

                String date;

                for (int j = 0; j < VALUE_DATE_INTERVAL; j++) {

                    date = mFormatter.print(mTime);

                    mPaintWords.getTextBounds(date, 0, date.length(), mBounds);
                    canvas.drawText(date,
                            (j * spaceBetweenDates) + getPaddingStart() / 2
                                    + spaceBetweenDates / 2 - mPaintWords.measureText(date) / 2,
                            getHeight() - mBounds.height() / 2,
                            mPaintWords);

                    mTime = mTime.plusMonths(1);
                }

                mTime = mTime.withMillis(mMinDate);
            }

            //Draw value by the bar

            mPaintWords.setTextSize(mValueTextSize);

            String value = String.valueOf((int) (topLevel - i) * VALUE_BAR_INTERVAL);
            mPaintWords.getTextBounds(value, 0, value.length(), mBounds);

            canvas.drawText(value,
                    0,
                    (i * spaceBetweenBars) + getPaddingStart()
                            - mBounds.height() / 2 - mPaintWords.ascent() - mPaintWords.descent(),
                    mPaintWords);
        }

        //Draw bar

        canvas.drawPath(mPathBars, mPaint);
    }

    private void drawGraph(Canvas canvas) {

        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        float intervalWidth = (float)
                (getWidth() - (getPaddingStart() * 2)) / mListIntervals.size();

        int counter = 0;

        while (counter < mListIntervals.size() / VALUE_ROUNDED_VALUE) {

            if (mSteps < mListIntervals.size()) {

                if (mSteps == 0) {
                    mPathGraph.moveTo(
                            getPaddingStart() + (intervalWidth * mSteps),
                            getHeight() - getPaddingStart()
                                    - (((getHeight() - getPaddingStart())
                                    * mListIntervals.get(mSteps).y) / mMaxValue));
                } else {
                    mPathGraph.lineTo(
                            getPaddingStart() + (intervalWidth * mSteps),
                            getHeight() - getPaddingStart()
                                    - (((getHeight() - getPaddingStart())
                                    * mListIntervals.get(mSteps).y) / mMaxValue));
                }
            }

            if (mSteps < mListIntervals.size()) {
                mSteps++;
            } else {
                break;
            }

            counter++;
        }

        canvas.drawPath(mPathGraph, mPaint);
    }

    private void getMaxMinValue() {

        float maxValue = mListIntervals.get(0).y;
        long minValue = mListIntervals.get(0).x;

        for (int i = 1; i < mListIntervals.size(); i++) {
            if (maxValue < mListIntervals.get(i).y) {
                maxValue = mListIntervals.get(i).y;
            }
            if (minValue > mListIntervals.get(i).x) {
                minValue = mListIntervals.get(i).x;
            }
        }
        mMaxValue = Math.round(maxValue) + VALUE_ROUNDED_VALUE;
        mMinDate = minValue * 1000;
    }
}
