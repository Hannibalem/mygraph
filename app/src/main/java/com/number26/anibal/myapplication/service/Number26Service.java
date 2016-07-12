package com.number26.anibal.myapplication.service;

import com.number26.anibal.myapplication.api.BaseResponse;
import com.number26.anibal.myapplication.model.Interval;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by anibal on 30.06.16.
 */
public interface Number26Service {

    @GET("charts/market-price")
    Observable<BaseResponse> getGraph(@Query("format") String format);
}
