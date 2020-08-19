package com.example.quiz.Controller.StringEncryption;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class StringEncryption {
    private static final String SECRET_KEY = "ThisIsASecretKey";//Replace with a more secure key

    public static String encryptString(String n) throws Exception{
        Key key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        String encryptedNickname = Base64.encodeToString(cipher.doFinal(n.getBytes()), Base64.DEFAULT);
        return encryptedNickname;
    }

    public static String decryptString(String n) throws Exception{
        Key key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        String decryptedNickname = new String(cipher.doFinal(Base64.decode(n, Base64.DEFAULT)));
        return decryptedNickname;
    }
}
