package com.cabot.androidtemplateproject.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

/**
 * Created by vaisakh on 22/8/17.
 */

class SecurityProvider {
    private static final String AndroidKeyStore = "AndroidKeyStore";
    private static final String AES_GCM_MODE = "AES/GCM/NoPadding";
    private static final String AES_MODE = "AES";
    private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
    private static final String ENCRYPTED_KEY = "ENCRYPTED_KEY";
    private static final String FIXED_IV = "16RandomVector";

    private KeyStore mKeyStore = null;
    private static SecurityProvider mInstance = null;

    public static SecurityProvider getInstance() {
        if (mInstance == null) {
            mInstance = new SecurityProvider();
        }
        return mInstance;
    }

    private SecurityProvider() {
        try {
            mKeyStore = KeyStore.getInstance(AndroidKeyStore);
            mKeyStore.load(null);
        } catch (Exception e) {

        }
    }

    public void generate(Context context, String keyStoreEntryName) {

        try {
            if (!mKeyStore.containsAlias(keyStoreEntryName)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore);
                    keyGenerator.init(
                            new KeyGenParameterSpec.Builder(keyStoreEntryName,
                                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                    .setRandomizedEncryptionRequired(false)
                                    .build());
                    keyGenerator.generateKey();
                } else {
//                    // Generate a key pair for encryption
                    Calendar start = Calendar.getInstance();
                    Calendar end = Calendar.getInstance();
                    end.add(Calendar.YEAR, 30);
                    //The app is not supported under api level 19
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                                .setAlias(keyStoreEntryName)
                                .setSubject(new X500Principal("CN=" + keyStoreEntryName + ", O=Android Authority"))
                                .setSerialNumber(BigInteger.ONE)
                                .setStartDate(start.getTime())
                                .setEndDate(end.getTime())
                                .build();
                        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", AndroidKeyStore);
                        kpg.initialize(spec);
                        kpg.generateKeyPair();
                    }
                }
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {

        }
    }

    public boolean alreadyHasEntryInKeystore(String keyStoreEntryName) {
        boolean exist = false;
        try {
            exist = mKeyStore.containsAlias(keyStoreEntryName);
        } catch (KeyStoreException e) {

        }
        return exist;
    }


    private Key getSecretKey(Context context, String keyStoreEntryName) throws Exception {
        String enryptedKeyB64 = getSecretBase64(context, keyStoreEntryName);
        byte[] encryptedKey = Base64.decode(enryptedKeyB64, Base64.DEFAULT);
        byte[] key;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            key = encryptedKey;
        } else {
            key = rsaDecrypt(keyStoreEntryName, encryptedKey);
        }
        return new SecretKeySpec(key, "AES");
    }

    public String getSecretBase64(Context context, String keyStoreEntryName) throws Exception {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String enryptedKeyB64 = pref.getString(ENCRYPTED_KEY, null);
        if (enryptedKeyB64 == null) {
            byte[] key = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(key);
            byte[] encryptedKey;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                encryptedKey = key;
            } else {
                encryptedKey = rsaEncrypt(keyStoreEntryName, key);
            }
            enryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString(ENCRYPTED_KEY, enryptedKeyB64);
            edit.apply();
        }
        return enryptedKeyB64;
    }

    public String getEncriptedData(Context context, String keyStoreEntryName, String input) throws Exception {
        Cipher c;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            c = Cipher.getInstance(AES_GCM_MODE);
            c.init(Cipher.ENCRYPT_MODE, getSecretKey(context, keyStoreEntryName), new GCMParameterSpec(128, FIXED_IV.getBytes()));
        } else {
            c = Cipher.getInstance(AES_MODE, "BC");
            c.init(Cipher.ENCRYPT_MODE, getSecretKey(context, keyStoreEntryName));
        }
        byte[] encodedBytes = c.doFinal(input.getBytes());
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
    }

    public String getDecryptedData(Context context, String keyStoreEntryName, String encrypted) throws Exception {
        byte[] encodedBytes = Base64.decode(encrypted, Base64.DEFAULT);
        Cipher c;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            c = Cipher.getInstance(AES_GCM_MODE);
            c.init(Cipher.DECRYPT_MODE, getSecretKey(context, keyStoreEntryName), new GCMParameterSpec(128, FIXED_IV.getBytes()));
        } else {
            c = Cipher.getInstance(AES_MODE, "BC");
            c.init(Cipher.DECRYPT_MODE, getSecretKey(context, keyStoreEntryName));
        }
        byte[] decodedBytes = c.doFinal(encodedBytes);
        return new String(decodedBytes);
    }

    private byte[] rsaEncrypt(String keyStoreEntryName, byte[] secret) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(keyStoreEntryName, null);
        Cipher inputCipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());
        return inputCipher.doFinal(secret);
    }

    private byte[] rsaDecrypt(String keyStoreEntryName, byte[] encrypted) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(keyStoreEntryName, null);
        Cipher output = Cipher.getInstance(RSA_MODE);
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
        byte[] decryptedData = output.doFinal(encrypted);
        return decryptedData;
    }
}
