package com.cabot.androidtemplateproject.modules.registration.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.cabot.androidtemplateproject.R;
import com.cabot.androidtemplateproject.databinding.ActivityRegistrationBinding;
import com.cabot.androidtemplateproject.modules.registration.model.RegistrationModel;
import com.cabot.androidtemplateproject.modules.registration.viewmodel.RegistrationViewModel;
import com.cabot.androidtemplateproject.security.EncryptionDecryption;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.NoSuchPaddingException;

public class RegistrationActivity extends AppCompatActivity implements RegistrationViewModel.RegistrationInteractionListener {

    RegistrationModel mRegistration;
    static File keyFileDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keyFileDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/key");
        keyFileDirectory.mkdirs();
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

    private void downloadPublicKeyFile() throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException {

        keyFileDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/key");
        keyFileDirectory.mkdirs();

        // read external storage permission should be asked before the implementation

        new DownloadPubFile().execute();

    }

    //Async task for downloading public file
    private class DownloadPubFile extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegistrationActivity.this);
            progressDialog.setMessage("Loading");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            Boolean isDownloadSuccess = downloadFileFromServer();
            if (isDownloadSuccess) {
                Log.d(String.valueOf(isDownloadSuccess), "Downloaded!!!");
            } else {
                Log.d(String.valueOf(isDownloadSuccess), "Download Failed!");

                Handler mainHandler = new Handler(RegistrationActivity.this.getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {

                        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                        builder.setTitle("Connection Failed!");

                        builder.setMessage("Attempt for a secure connection to the server failed. Please retry.");
                        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadFileFromServer();
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Exit", "from the App Here!!");
                            }
                        });
                        builder.setCancelable(false);
                        AlertDialog alert = builder.create();
                        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                        alert.show();
                    }
                };
                mainHandler.post(myRunnable);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            super.onPostExecute(s);
            try {
                loadInitializeApi();
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }
        }
    }

    //Download File
    public static boolean downloadFileFromServer() {
        int count;
        try {
            URL url = new URL("/place your url here/");
            URLConnection connection = url.openConnection();
            connection.connect();

            // Download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

            // Output stream
            OutputStream output = new FileOutputStream(keyFileDirectory.toString() + "/request_pub.der");

            byte data[] = new byte[2048];

            while ((count = input.read(data)) != -1) {

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

            Log.d("File Download", "Success!!!");

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private void loadInitializeApi() throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
        EncryptionDecryption encryDecryUtil = new EncryptionDecryption(getApplicationContext());
        String key = EncryptionDecryption.getAesKey(RegistrationActivity.this); //32 bytes = 256 bit
        String encryptedKey = "";
        if (key != null) {
            Log.d("AES symmetric key=", key);

            encryptedKey = encryDecryUtil.encryptRequest(key);
            Log.d("encrypted text=", encryptedKey);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
            builder.setTitle("Connection Failed!");
            builder.setMessage("Attempt for a secure connection to the server failed. Please retry.");
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downloadFileFromServer();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("Exit", "from the App Here!!");
                    finish();
                }
            });
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            alert.show();
            return;
        }
       /*
       do api stuffs
        */
    }
}
