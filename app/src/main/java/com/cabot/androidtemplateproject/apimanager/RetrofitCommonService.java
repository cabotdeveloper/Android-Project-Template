package com.cabot.androidtemplateproject.apimanager;

import com.cabot.androidtemplateproject.constants.Constants;
import com.cabot.androidtemplateproject.modules.registration.model.Initialization;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RetrofitCommonService {

    //Initialize App
    @FormUrlEncoded
    @Headers("Accept:application/json")
    @POST("initializeApp")
    Call<Initialization> doInitialize(@Field(Constants.DEVICE_ID_FIELD_KEY) String deviceId);
}
