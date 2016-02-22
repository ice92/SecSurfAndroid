package com.ftunram.secsurf.toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static void encrypt(String key, File inputFile, File outputFile) throws CryptoException {
        doCrypto(1, key, inputFile, outputFile);
    }

    public static void decrypt(String key, File inputFile, File outputFile) throws CryptoException {
        doCrypto(2, key, inputFile, outputFile);
    }

    private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws CryptoException {
        Exception ex;
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), TRANSFORMATION);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[((int) inputFile.length())];
            inputStream.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            inputStream.close();
            outputStream.close();
        } catch (NoSuchPaddingException e) {
            ex = e;
            throw new CryptoException("Error encrypting/decrypting file", ex);
        } catch (NoSuchAlgorithmException e2) {
            ex = e2;
            throw new CryptoException("Error encrypting/decrypting file", ex);
        } catch (InvalidKeyException e3) {
            ex = e3;
            throw new CryptoException("Error encrypting/decrypting file", ex);
        } catch (BadPaddingException e4) {
            ex = e4;
            throw new CryptoException("Error encrypting/decrypting file", ex);
        } catch (IllegalBlockSizeException e5) {
            ex = e5;
            throw new CryptoException("Error encrypting/decrypting file", ex);
        } catch (IOException e6) {
            ex = e6;
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
}
