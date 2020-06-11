package com.liquor.launcher.security;

import com.liquor.launcher.Liquor;
import com.liquor.prerequisites.openvpn.OpenVPNResource;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.stream.Collectors;

public class Decrypter {

    private static Decrypter instance;

    private Cipher cipher;

    private IvParameterSpec IV;

    private SecretKeySpec secretKeySpec;

    public Decrypter() {
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

    @SneakyThrows
    public void encryptConfigs() {
        URI configDirectory = Liquor.class.getResource(OpenVPNResource.OPENVPN_CONFIG_PATH).toURI();
        Files.walkFileTree(Paths.get(configDirectory), new SimpleFileVisitor<Path>() {
            @SneakyThrows
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.getFileName().toString().endsWith(".ovpn")) {
                    String encryptedLines = Files.lines(file).map(line -> encrypt(line)).collect(Collectors.joining());
                    String encrypted = encrypt(encryptedLines);
                    System.out.print(file.getFileName() + ": ");
                    System.out.println(encrypted);
                }
                return FileVisitResult.CONTINUE;
            }
        });
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