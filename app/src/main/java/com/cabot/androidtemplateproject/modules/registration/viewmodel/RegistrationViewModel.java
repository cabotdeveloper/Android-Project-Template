package com.cabot.androidtemplateproject.modules.registration.viewmodel;

import android.content.Context;
import android.view.View;

import com.cabot.androidtemplateproject.Utilities.AppUtilis;
import com.cabot.androidtemplateproject.apimanager.ApiManager;
import com.cabot.androidtemplateproject.apimanager.ApiPath;
import com.cabot.androidtemplateproject.modules.registration.model.InitializationRequest;
import com.cabot.androidtemplateproject.modules.registration.model.RegistrationModel;

import retrofit2.Response;

/**
 * Created by neethu on 18/7/17.
 */

public class RegistrationViewModel implements ApiManager.ApiResponseListener{

    private RegistrationModel mRegistration;
    private RegistrationInteractionListener mListener;
    private Context mContext;
    private ApiManager apiManager;

    public RegistrationViewModel(Context context) {
        mContext = context;
        mListener = (RegistrationInteractionListener) context;
        mRegistration =new RegistrationModel();
        apiManager = new ApiManager(context, this);
        initializeApp();
    }

    /**
     * Call API: Initialize App
     */
    public void initializeApp() {
        if (!AppUtilis.isOnline(mContext)) {

            //Request Model
            InitializationRequest request = new InitializationRequest(mContext);

            //Call Initialize API
            apiManager.initializeApp(request);
        }
    }

    /**
     * Handle Continue button click
     *
     * @param view The view clicked
     */
    public void onContinueClick(final View view) {
       /**
        * add the action here
        **/
    }

    public void onFirstNameChanged(CharSequence firstName, int start, int before, int count) {
        mRegistration.setFirstName(firstName.toString());
    }

    public void onLastNameChanged(CharSequence lastName, int start, int before, int count) {
        mRegistration.setLastName(lastName.toString());
    }

    public void onMobileNumberChanged(CharSequence mobileNumber, int start, int before, int count) {
        mRegistration.setMobileNumber(mobileNumber.toString());
    }

    /**
     * The interface for communicating with the view
     */
    public interface RegistrationInteractionListener {

        void onInitializationSuccess();
        void onInitializationFailure();
        void onStartRegistration();
        void onFailureRegistration();
        void onSuccessRegistration();
    }

    @Override
    public void onApiCallSuccess(ApiPath path, Response response) {

        mListener.onInitializationSuccess();

    }

    @Override
    public void onApiCallFailure(ApiPath path) {
        mListener.onInitializationFailure();

    }
}
