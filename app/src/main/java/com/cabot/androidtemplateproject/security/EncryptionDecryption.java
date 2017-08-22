package com.cabot.androidtemplateproject.security;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * Created by vaisakh on 22/8/17.
 */

public class EncryptionDecryption {
    private static final int REQUEST_FOR_STORAGE_PERMISSION = 123;
    static KeyPairGenerator kpg;
    static KeyPair kp;
    static PublicKey publicKey, storedPublicKey;
    static PrivateKey privateKey, storedPrivateKey;
    byte[] encryptedBytes, decryptedBytes;
    static Cipher cipher, cipher1;
    static String decrypted;

    //url to download public key file from server
    static String sourceUrl = "http://socialpayappcms.com/cert/requestKeys/request_pub.der";//Live
    static File keyFileDirectory;
    static KeyStore keystore = null;
    static String chatKey = "BxTCgXSImuETh2LXj7XkZhJbeIA01SBI";

    public EncryptionDecryption(Context context) {

        keyFileDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/key");
        keyFileDirectory.mkdirs();
    }

    //get generated public key
    public String getResponsePublicKey() {
        byte[] publicKeyBytes = Base64.encode(storedPublicKey.getEncoded(), 0);
        String pubKey = new String(publicKeyBytes);
        return pubKey;
    }

    public String encryptRequest(String data/*, Activity activity*/) {

        //Read Public Key From File
        String encryptedJson = null;
        File file = new File(keyFileDirectory.toString() + "/request_pub.der");
        try {

            FileInputStream fin = new FileInputStream(file.getAbsolutePath().toString());
            CertificateFactory f = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) f.generateCertificate(fin);
            publicKey = certificate.getPublicKey();
            encryptedJson = encrypt(data, publicKey);
            return encryptedJson;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String encrypt(String jsonObject, PublicKey pubKey) {

        //Convert Json Object to Base64
        String base64 = null;
        try {
            base64 = Base64.encodeToString(RSAEncrypt(jsonObject, pubKey), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        Log.d("Base 64: ", base64);
        return base64;
    }

    //encrypt given json request
    public byte[] RSAEncrypt(final String plain, final PublicKey rsaPublic) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ArithmeticException {

        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublic);
        encryptedBytes = cipher.doFinal(plain.getBytes());
        return encryptedBytes;
    }

    //runtime permission for write external storage
    protected static void shouldAskPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{READ_EXTERNAL_STORAGE}, REQUEST_FOR_STORAGE_PERMISSION);
            return;
        }

        if ((ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{READ_EXTERNAL_STORAGE},
                        REQUEST_FOR_STORAGE_PERMISSION);

            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{READ_EXTERNAL_STORAGE},
                        REQUEST_FOR_STORAGE_PERMISSION);
            }
        } else {
            DownloadFiles();
        }
    }


    public static void DownloadFiles() {

        try {
            URL u = new URL(sourceUrl);
            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            FileOutputStream fos = new FileOutputStream(keyFileDirectory.toString() + "/request_pub.der");
            while ((length = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
    }


    //Download File
    public static boolean downloadFileFromServer() {
        int count;
        try {
            URL url = new URL(sourceUrl);
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

            return true;

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            return false;
        }
    }

    //Async task for downloading public file
    private static class DownloadPubFile extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Boolean isDownloadSuccess = downloadFileFromServer();
            if (isDownloadSuccess) {
                Log.d(String.valueOf(isDownloadSuccess), "Downloaded!!!");
            } else {
                Log.d(String.valueOf(isDownloadSuccess), "Download Failed!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    /**
     * to decrypt the server response
     **/

    public static JSONObject getActualResponse(JSONObject jsonResponse, Context context) throws Exception/*NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, JSONException */ {
        /*String key = getAesKey(context);*/
        String key = getSavedKey(context, " API_AES_KEY_SECRET_PREF_KEY");
        Log.d("AES decryption key:", key);
        String iv = key.substring(0, 16);
        String encryptedData = URLDecoder.decode(jsonResponse.getString("encrypted data"), "UTF-8");
        Log.d("encrypted data:", encryptedData);
        CryptLib cryptLib = new CryptLib();
        String decryptedData = cryptLib.decrypt(encryptedData, key, iv);
        JSONObject responseJson = new JSONObject(decryptedData);
        return responseJson;
    }

    public static JSONObject getEncryptedRequest(JSONObject jsonRequest, Context context) throws Exception/*NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException */ {
       /* String key = getAesKey(context);*/
        String key = getSavedKey(context, "/key/");
        Log.d("AES symmetric key= ", key);
        String iv = key.substring(0, 16);
        String responseString = jsonRequest.toString();
        CryptLib cryptLib = new CryptLib();
        String encryptedData = cryptLib.encrypt(responseString, key, iv);
        String urlEncodedString = URLEncoder.encode(encryptedData, "UTF-8");
        Map<String, String> data = new HashMap<>();
        data.put("encrypted data key", urlEncodedString);

        JSONObject jsonData = new JSONObject(data);
        return jsonData;
    }

    public static String getAesKey(Context context) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException {
        CryptLib cryptLib = new CryptLib();
        String key = cryptLib.SHA256(getRandomString(10), 32);  //32 bytes = 256 bit
        setSavedKey(context, key, "key file API_AES_KEY_SECRET_PREF_KEY");
        Log.d("AES Key Generated: ", key);

        return key;
    }

    public static JSONObject getChatEncryptedRequest(JSONObject jsonRequest, Context context)
            throws Exception {
        String key = getSavedKey(context, "CHAT_AES_KEY_SECRET_PREF_KEY");
        String iv = getSavedIv(context, "CHAT_AES_IV_SECRET_PREF_KEY");
        String responseString = jsonRequest.toString();
        CryptLib cryptLib = new CryptLib();
        String encryptedData = cryptLib.encrypt(responseString, key, iv);
        String urlEncodedString = URLEncoder.encode(encryptedData, "UTF-8");
        Map<String, String> data = new HashMap<>();
        data.put("your key", "your value");
        data.put("your key", "your value");

        JSONObject jsonData = new JSONObject(data);
        return jsonData;
    }

    public static String getChatEncryptedMessage(String message, Context context) throws
            Exception {

        String key = getSavedKey(context, "CHAT_AES_KEY_SECRET_PREF_KEY");
        String iv = getSavedIv(context, "CHAT_AES_IV_SECRET_PREF_KEY");

        Log.d("AES key CHAT=", key);
        Log.d("AES IV CHAT=", iv);
        CryptLib cryptLib = new CryptLib();
        String encryptedData = cryptLib.encrypt(message.trim(), key, iv);
        Log.d("msg encrypted=", encryptedData);
        return encryptedData;
    }

    public static String getChatActualMessage(String message, Context context) throws Exception {

        String key = getSavedKey(context, "CHAT_AES_KEY_SECRET_PREF_KEY");
        String iv = getSavedIv(context, "CHAT_AES_IV_SECRET_PREF_KEY");
        Log.d("encrypted data:", message);
        CryptLib cryptLib = new CryptLib();
        String decryptedData = cryptLib.decrypt(message.trim(), key, iv);
        return decryptedData;
    }

    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static void setSavedKey(Context c, String savedPassword, String keyEntry) {
        if (!SecurityProvider.getInstance().alreadyHasEntryInKeystore(keyEntry)) {
            SecurityProvider.getInstance().generate(c, keyEntry);
        }
        if (savedPassword != null) {
            try {
                savedPassword = SecurityProvider.getInstance().getDecryptedData(c, keyEntry, savedPassword);
            } catch (Exception e) {

            }
        }
        PreferenceManager.getDefaultSharedPreferences(c).edit()
                .putString(keyEntry, savedPassword).apply();
    }

    public static String getSavedKey(Context c, String keyEntry) {
        if (!SecurityProvider.getInstance().alreadyHasEntryInKeystore(keyEntry)) {
            SecurityProvider.getInstance().generate(c, keyEntry);
        }
        String input = PreferenceManager.getDefaultSharedPreferences(c).getString(
                keyEntry, null);
        if (input != null) {
            try {
                input = SecurityProvider.getInstance().getDecryptedData(c, keyEntry, input);
            } catch (Exception e) {

            }
        }
        return input;
    }

    public static void setSavedIv(Context c, String savedPassword, String keyEntry) {
        if (!SecurityProvider.getInstance().alreadyHasEntryInKeystore(keyEntry)) {
            SecurityProvider.getInstance().generate(c, keyEntry);
        }
        if (savedPassword != null) {
            try {
                savedPassword = SecurityProvider.getInstance().getDecryptedData(c, keyEntry, savedPassword);
            } catch (Exception e) {

            }
        }
        PreferenceManager.getDefaultSharedPreferences(c).edit()
                .putString(keyEntry, savedPassword).apply();
    }

    public static String getSavedIv(Context c, String keyEntry) {
        if (!SecurityProvider.getInstance().alreadyHasEntryInKeystore(keyEntry)) {
            SecurityProvider.getInstance().generate(c, keyEntry);
        }
        String input = PreferenceManager.getDefaultSharedPreferences(c).getString(
                keyEntry, null);
        if (input != null) {
            try {
                input = SecurityProvider.getInstance().getDecryptedData(c, keyEntry, input);
            } catch (Exception e) {

            }
        }
        return input;
    }
}
