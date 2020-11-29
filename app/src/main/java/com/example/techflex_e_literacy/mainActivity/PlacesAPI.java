package com.example.techflex_e_literacy.mainActivity;

import com.example.techflex_e_literacy.pojos.Places;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PlacesAPI {

    @GET("maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&types=food&name=harbour&key=AIzaSyCpsC6d2-HKxVUlIDR_fmwFC-d5u-K1YV4")
    Call<Places> getPlaces();
}
