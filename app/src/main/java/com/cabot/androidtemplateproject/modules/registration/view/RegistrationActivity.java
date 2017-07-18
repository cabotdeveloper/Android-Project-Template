package com.cabot.androidtemplateproject.modules.registration.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cabot.androidtemplateproject.R;
import com.cabot.androidtemplateproject.databinding.ActivityRegistrationBinding;
import com.cabot.androidtemplateproject.modules.registration.model.RegistrationModel;
import com.cabot.androidtemplateproject.modules.registration.viewmodel.RegistrationViewModel;

public class RegistrationActivity extends AppCompatActivity implements RegistrationViewModel.RegistrationInteractionListener {

    RegistrationModel mRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegistration = new RegistrationModel();
        ActivityRegistrationBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        mBinding.setRegistration(new RegistrationViewModel(this));
    }

    @Override
    public void onStartRegistration() {
        //ADD:- Handle registration progress

    }

    @Override
    public void onFailureRegistration() {
        //ADD:- Handle Failure
    }

    @Override
    public void onSuccessRegistration() {
        //ADD:- Handle registration success

    }

    @Override
    public void onInitializationSuccess() {
        // Add code to handle success
    }

    @Override
    public void onInitializationFailure() {
        // Add code to handle failure
    }
}
