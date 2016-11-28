package com.p2p.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import Decoder.BASE64Decoder;


public class EncryptionUtil {

  /* String to hold name of the encryption algorithm.*/
  public static final String ALGORITHM = "RSA";

  /*String to hold the name of the private key file.*/
  public static final String PRIVATE_KEY_FILE = "./private.key";
  //public static final String PRIVATE_KEY_FILE = " C:/Users/SAIRAMUDAYAJANARDHAN/Desktop/Vathsa/private.key";

  /*String to hold name of the public key file.*/
  public static final String PUBLIC_KEY_FILE = "C:/keys/public.key";
  //public static final String PUBLIC_KEY_FILE = " C:/Users/SAIRAMUDAYAJANARDHAN/Desktop/Vathsa/public.key";

  
  /**
   * Generate key which contains a pair of private and public key using 1024
   * bytes. Store the set of keys in Prvate.key and Public.key files.
   * 
   * @throws NoSuchAlgorithmException
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static void generateKey() {
    try {
      final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
      keyGen.initialize(1024);
      final KeyPair key = keyGen.generateKeyPair();

      File privateKeyFile = new File(PRIVATE_KEY_FILE);
      File publicKeyFile = new File(PUBLIC_KEY_FILE);

      // Create files to store public and private key
      if (privateKeyFile.getParentFile() != null) {
        privateKeyFile.getParentFile().mkdirs();
      }
      privateKeyFile.createNewFile();

      if (publicKeyFile.getParentFile() != null) {
        publicKeyFile.getParentFile().mkdirs();
      }
      publicKeyFile.createNewFile();

      // Saving the Public key in a file
      ObjectOutputStream publicKeyOS = new ObjectOutputStream(
          new FileOutputStream(publicKeyFile));
      publicKeyOS.writeObject(key.getPublic());
      publicKeyOS.close();

      // Saving the Private key in a file
      ObjectOutputStream privateKeyOS = new ObjectOutputStream(
          new FileOutputStream(privateKeyFile));
      privateKeyOS.writeObject(key.getPrivate());
      privateKeyOS.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * The method checks if the pair of public and private key has been generated.
   * @return flag indicating if the pair of keys were generated.
   */
  public static boolean areKeysPresent() {

    File privateKey = new File(PRIVATE_KEY_FILE);
    File publicKey = new File(PUBLIC_KEY_FILE);

    if (privateKey.exists() && publicKey.exists()) {
      return true;
    }
    return false;
  }

  /**
   * Encrypt the plain text using public key.
   * 
   * @param text
   *          : original plain text
   * @param key
   *          :The public key
   * @return Encrypted text
   * @throws java.lang.Exception
   */
  public static byte[] encrypt(String text, PublicKey key) {
    byte[] cipherText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      // encrypt the plain text using the public key
      cipher.init(Cipher.ENCRYPT_MODE, key);
      cipherText = cipher.doFinal(text.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cipherText;
  }

  /**
   * Decrypt text using private key.
   * 
   * @param text
   *          :encrypted text
   * @param key
   *          :The private key
   * @return plain text
   * @throws java.lang.Exception
   */
  public static String decrypt(byte[] text, PrivateKey key) {
    byte[] dectyptedText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance(ALGORITHM);

      // decrypt the text using the private key
      cipher.init(Cipher.DECRYPT_MODE, key);
      dectyptedText = cipher.doFinal(text);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return new String(dectyptedText);
  }
  
  public static PublicKey getPublicKey(String publicKeyStr) throws Exception{
	  //System.out.println("publicKeyStr in getPublicKey() before replacing: "+publicKeyStr);
	  publicKeyStr = publicKeyStr.replace("-----BEGIN PUBLIC KEY-----", "");
	  publicKeyStr = publicKeyStr.replace("-----END PUBLIC KEY-----", "");
	  //System.out.println("publicKeyStr in getPublicKey() after replacing: "+publicKeyStr);
	  	BASE64Decoder decoder = new BASE64Decoder();
		//byte[] keyBytes = decoder.decodeBuffer(pubKeyPEM);
		byte[] keyBytes = decoder.decodeBuffer(publicKeyStr);
		// generate public key
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey pubKey = keyFactory.generatePublic(spec);
		return pubKey;
  }
  
  public static PrivateKey getPrivateKey(String privateKeyStr) throws Exception{
	  byte [] encoded = Base64.decodeBase64(privateKeyStr.getBytes());
	  // PKCS8 decode the encoded RSA private key
	
	  PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
	  KeyFactory kf;
	  PrivateKey privKey;
	  kf = KeyFactory.getInstance("RSA");
	  privKey = kf.generatePrivate(keySpec);
	  return privKey;
  }
  
  public static String randomAESKeyGenerator() {
	  byte[] r = new byte[16]; //Means 2048 bit
	  Random rand = new Random();
	  rand.nextBytes(r);
	  String str = new String(r);
	  
	  System.out.println("Random string: "+str);
	  return str;
  }
  public static void randomAES() {
	  try {

		      // Check if the pair of keys are present else generate those.
		      if (!areKeysPresent()) {
		        // Method generates a pair of keys using the RSA algorithm and stores it
		        // in their respective files
		        generateKey();
		      }
		      randomAESKeyGenerator();
		      final String originalText = "Mary has one cat";
		      
			  File inputFile = new File("testFile.txt");
			  File encryptedFile = new File("document.encrypted");
			  File decryptedFile = new File("document.decrypted");
			 
			  /*try{
				  SymmetricEncryption.encrypt(originalText, inputFile, encryptedFile);
				  //Symmetric.decrypt(originalText, encryptedFile, decryptedFile);
			  }catch (CryptoException ex) {
			      System.out.println(ex.getMessage());
			      ex.printStackTrace();
			  }*/
			  System.out.println("Encrypted file generated");
			  
		      ObjectInputStream inputStream = null;

		      // Encrypt the string using the public key
		      inputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
		      System.out.println("inputStream: "+inputStream);
		      final PublicKey publicKey = (PublicKey) inputStream.readObject();
		      
		      final byte[] cipherText = encrypt(originalText, publicKey);
		      
		      System.out.println("cipherText.length: "+cipherText.length);
		      System.out.println("cipherText : "+Arrays.toString(cipherText));
		      // Decrypt the cipher text using the private key.
		      inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
		      final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
		      final String plainText = decrypt(cipherText, privateKey);

		      // Printing the Original, Encrypted and Decrypted Text
		      System.out.println("Original Symmetric key: " + originalText);
		      System.out.println("Encrypted Symmetric key: " +cipherText.toString());
		      System.out.println("Decrypted Symmetric key: " + plainText);
		      
		      /*try{
				  //Symmetric.encrypt(originalText, inputFile, encryptedFile);
				  SymmetricEncryption.decrypt(plainText, encryptedFile, decryptedFile);
			  }catch (CryptoException ex) {
			      System.out.println(ex.getMessage());
			      ex.printStackTrace();
			  }*/
			  System.out.println("Decrypted file generated");
		      

		    } catch (Exception e){
		      e.printStackTrace();
		    }
		  }
}
  
  
  
  