package com.liquor.launcher.security;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class Decrypter {

    private static Decrypter instance;

    private Cipher cipher;

    private IvParameterSpec IV;

    private SecretKeySpec secretKeySpec;

    private Decrypter() {
        initialize();
    }

    private void initialize() {
        try {
            final byte[] salt = "@1jq3#-o1_uHvaL:".getBytes();
            final String key = "hehexd";
            final int iterationCount = 12;
            final int keyStrength = 256;
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[cipher.getBlockSize()];
            IV = new IvParameterSpec(iv);
            this.secretKeySpec = new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(new PBEKeySpec(key.toCharArray(), salt, iterationCount, keyStrength)).getEncoded(), "AES");
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    //initialisation vector
    public final String encrypt(String data) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, IV);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public final String decrypt(String base64EncryptedData) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IV);
            return new String(cipher.doFinal(Base64.getDecoder().decode(base64EncryptedData)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Decrypter getInstance() {
        return instance == null ? instance = new Decrypter() : instance;
    }
}