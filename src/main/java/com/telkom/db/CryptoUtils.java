package com.telkom.db;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.SecureRandom;

public class CryptoUtils {
    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int TAG_LENGTH = 128;
    private static final int IV_SIZE = 12;
    private static final String SECRET_KEY = "01234567890123456789012345678901"; // 32 chars = 256-bit key

    public static SecretKey getKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
    }

    public static String[] encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(), spec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

        return new String[]{
                Base64.getEncoder().encodeToString(ciphertext),
                Base64.getEncoder().encodeToString(iv)
        };
    }

    public static String decrypt(String base64CipherText, String base64Iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        byte[] iv = Base64.getDecoder().decode(base64Iv);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec);

        byte[] decoded = Base64.getDecoder().decode(base64CipherText);
        byte[] plaintext = cipher.doFinal(decoded);
        return new String(plaintext);
    }
}
