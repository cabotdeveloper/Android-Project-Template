package com.cabot.androidtemplateproject.apimanager;

import android.content.Context;

import com.cabot.androidtemplateproject.modules.registration.model.InitializationRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.cabot.androidtemplateproject.apimanager.ApiPath.INITIALIZE;

/**
 * Created by neethu on 18/7/17.
 *
 * recreated to handle
 * the api calls from a single session
 */

public class ApiManager {

    private ApiResponseListener mListener;
    private RetrofitCommonService service;

    public ApiManager(Context context, ApiResponseListener listener) {
        service = RetrofitClient.getInstance();
        mListener = listener;

    }

    public void initializeApp(InitializationRequest request) {

        Call call = service.doInitialize(
                request.getDeviceId()
        );

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                mListener.onApiCallSuccess(INITIALIZE, response);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                call.cancel();
                mListener.onApiCallFailure(INITIALIZE);
            }
        });
    }

    /**
     * Interface for Communicating with View Model
     */
    public interface ApiResponseListener {
        void onApiCallSuccess(ApiPath path, Response response);
        void onApiCallFailure(ApiPath path);
    }
}
