package com.github.diwakar1988.junket.net.service;

import com.github.diwakar1988.junket.net.service.response.PhotoServiceResponse;
import com.github.diwakar1988.junket.net.service.response.TipServiceResponse;
import com.github.diwakar1988.junket.net.service.response.VenueServiceResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by diwakar.mishra on 02/12/16.
 */

public interface APIService {


    @GET("v2/venues/explore")
    Call<VenueServiceResponse> listVenues(@QueryMap Map<String, String> options);

    @GET("v2/venues/{VENUE_ID}/photos")
    Call<PhotoServiceResponse> listPhotos(@Path("VENUE_ID") String venueId, @QueryMap Map<String, String> options);

    @GET("v2/venues/{VENUE_ID}/tips")
    Call<TipServiceResponse> listTips(@Path("VENUE_ID") String venueId, @QueryMap Map<String, String> options);

    @POST("v2/venues/{VENUE_ID}/dislike")
    Call<String> dislike(@Path("VENUE_ID") String venueId, @QueryMap Map<String, String> options);

}
