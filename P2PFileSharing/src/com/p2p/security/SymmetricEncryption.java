package com.p2p.security;

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
import javax.crypto.spec.*;


public class SymmetricEncryption {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
 
    /*private static void doCrypto(int cipherMode, String key, File inputFile,
            File outputFile) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
             
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
             
            byte[] outputBytes = cipher.doFinal(inputBytes);
             
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
             
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }*/
    
    private static byte[] doCrypto(int cipherMode, String key, byte[] inputBytes) throws CryptoException {
        
    	try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
            
            byte[] outputBytes = cipher.doFinal(inputBytes);
            return outputBytes; 
             
        } catch (Exception e) {
            throw new CryptoException("Error encrypting/decrypting file", e);
        }
    }
    
    /*public static void encrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }
 
    public static void decrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }*/
    
    public static byte[] encrypt(String key, byte[] inputBytes)
            throws CryptoException {
        return doCrypto(Cipher.ENCRYPT_MODE, key, inputBytes);
    }
 
    public static byte[] decrypt(String key, byte[] inputBytes)
            throws CryptoException {
        return doCrypto(Cipher.DECRYPT_MODE, key, inputBytes);
    }
}
    
