package com.example.techflex_e_literacy.fragments;

import com.example.techflex_e_literacy.Notifications.MyResponse;
import com.example.techflex_e_literacy.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAATuoiuwM:APA91bHbLOgFhPXo7aEA2TKeOvRqAuYIYA3ymopdhr7nqihJXBWA8lTxicd2R4gQdQ_WYllSVNihuLW_QUxU3TTPPXE4penboWC0VYN-rdRolgj54FK7hFLEuRmWKD_l1iDoV7H2bgQA"
            }

    )
    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body Sender body);
}
