package com.p2p.dht;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import net.tomp2p.storage.TrackerData;

import com.p2p.security.CryptoException;
import com.p2p.security.EncryptionUtil;
import com.p2p.security.SymmetricEncryption;

public class ChunkThread implements Runnable{
	public Thread chunkThread;
	private String threadName;
	private TrackerData trackerData;
	private String reqKey;
	private long chunkSize;
	private TrustFactorPlusIP peerTrustFactorDetails;
	private String privateKeyStr;
	private boolean enableAESPadding;
	
	public ChunkThread(String _threadName, TrackerData _trackerData, String _reqKey, long _chunkSize, 
			TrustFactorPlusIP _peerTrustFactorDetails, String _privateKeyStr, boolean _enableAESPadding) {
		// TODO Auto-generated constructor stub
		threadName = _threadName;
		trackerData = _trackerData;
		reqKey = _reqKey;
		chunkSize = _chunkSize;
		peerTrustFactorDetails = _peerTrustFactorDetails;
		privateKeyStr = _privateKeyStr;
		enableAESPadding = _enableAESPadding;
		System.out.println("ThreadName: "+threadName);
		//System.out.println("ThreadName: "+threadName);
	}
	
	public void start(){
		if(chunkThread == null){
			chunkThread = new Thread(this,  threadName);
			chunkThread.start();
		}
	}
	
	@Override
	public void run() {
		try{
			
			long startTime = System.currentTimeMillis();
			  
			// TODO Auto-generated method stub
			Socket client = new Socket(trackerData.getPeerAddress().getInetAddress(), trackerData.getPeerAddress().portUDP()+1);
	 		DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
	 		
	 		dataOutputStream.writeBytes(reqKey+"\n");
	 		//dataOutputStream.writeBytes(cmdArr[1]+"_Size");
	 		//System.out.println("Sent the fileName_Part<i>");
	 		
	 		System.out.println("Requesting Peer: "+trackerData.getPeerAddress().getInetAddress()+" for "+reqKey);
	 		
	 		InputStream inputStream = client.getInputStream();
	 		
	 		PrivateKey privateKey = EncryptionUtil.getPrivateKey(privateKeyStr);
	 		byte[] encryptedAESKeyBytes = new byte[256];
	 		
	 		//final String AESKey = decrypt(cipherText, privKey);
	 		String AESKey = " "; // it has to retrieved from FileServerThread
	 		
	 		// waiting for seeder to send AESKey
	 		if((inputStream.read(encryptedAESKeyBytes))!= -1){
	 			AESKey = EncryptionUtil.decrypt(encryptedAESKeyBytes, privateKey);
	 		}
	 		
	 		dataOutputStream.writeBytes("Ok\n");
	 		System.out.println("Sending ACK for encryptedAESKey: "+AESKey);
	 		
	 		System.out.println("chunkSize long: "+chunkSize);
	 		System.out.println("chunkSize long typecasted with int: "+(int)chunkSize);
	 		
	 		byte[] chunkSizeBytes = new byte[(int)chunkSize];
	 		
	 		int numBytesRead = 0;
	 		int chunkSizeRemaining = (int)chunkSize;
	 		
 			// if tmp_fileName directory is not there, creating tmp_fileName directory
	 		File folder = new File("./download/tmp_"+reqKey.split("_")[0]);
	 		if(!folder.exists()){
	 			folder.mkdir();
	 		}
	 		
	 		//final String AESKey = EncryptionUtil.randomAESKeyGenerator();
	 		
	 		FileOutputStream outputStream = new FileOutputStream(new File("./download/tmp_"+reqKey.split("_")[0]+"/tmp_"+reqKey),true);
	 		
	 		while(chunkSizeRemaining > 0){
	 			if((numBytesRead = inputStream.read(chunkSizeBytes)) != -1){
		 			//System.out.println("Inside if condition");
		 			//System.out.println("after reading data contents: "+Arrays.toString(data));
		 			//System.out.println("fileName from reqKey: " + reqKey.split("_")[0]);
		 			

		 			
		 			
		 			/*if(numBytesRead < chunkSize){
		 				System.out.println("Inside small chunks, numBytesRead: "+numBytesRead);
		 				byte[] chunkSizeBytesRead = Arrays.copyOf(chunkSizeBytes, numBytesRead);
		 				outputStream.write(chunkSizeBytesRead);
	    				
		 			}else{
		 				System.out.println("Inside proper chunks, numBytesRead: "+numBytesRead);
		 				outputStream.write(chunkSizeBytes);
		 			}*/
		 			
	 				//System.out.println("Inside chunks, numBytesRead: "+numBytesRead);
	 				byte[] chunkSizeBytesRead = Arrays.copyOf(chunkSizeBytes, numBytesRead);
	 				// add decrypt call to decrypt the incoming bytes and save output bytes using outputstream into file
	 				
	 				byte[] outputBytes = new byte[chunkSizeBytesRead.length];
	 				
	 				/*try{
	 					outputBytes = SymmetricEncryption.decrypt(AESKey, chunkSizeBytesRead);
	 					  //Symmetric.decrypt(originalText, encryptedFile, decryptedFile);
	 				}catch (CryptoException ex) {
	 				      System.out.println(ex.getMessage());
	 				      ex.printStackTrace();
	 				}*/
	 				
	 				//outputStream.write(chunkSizeBytesRead);
	 				outputStream.write(chunkSizeBytesRead);
			 		
			 		//System.out.println("Leaving if condition");
		 		}
	 			chunkSizeRemaining -= numBytesRead;
	 		}
	 		
	 		outputStream.close();
	 		File inputFile = new File("./download/tmp_"+reqKey.split("_")[0]+"/tmp_"+reqKey);
	 		//File outputFile = new File("./download/tmp_"+reqKey+"_dec");
	 		FileInputStream finStream = new FileInputStream(inputFile);
	 		
	        byte[] inputBytes = new byte[(int) inputFile.length()];
	        finStream.read(inputBytes);
	        finStream.close();
	        
	        FileOutputStream foutStream = new FileOutputStream(inputFile);
	        byte[] outputBytes = new byte[(int) inputFile.length()];
				
			try{
				outputBytes = SymmetricEncryption.decrypt(AESKey, inputBytes, enableAESPadding);
				  //Symmetric.decrypt(originalText, encryptedFile, decryptedFile);
			}catch (CryptoException ex) {
			      System.out.println(ex.getMessage());
			      ex.printStackTrace();
			}
	 		
			//String outStr = new String(outputBytes, "UTF-8"); // for UTF-8 encoding
 			//System.out.println("outStr: "+outStr);
			
			foutStream.write(outputBytes);
			foutStream.close();
			
	 		//Thread.currentThread().destroy();

	        //System.out.println("Server says ");
	 		
	 		//Thread.sleep(100);
	 		//client.close();
	 		long endTime = System.currentTimeMillis();
	 		long diffTime = endTime - startTime;
	 		double downloadSpeed = ((double)(chunkSize*1000)/(double)(diffTime));
	 		System.out.println("StartTime: "+startTime);
	 		System.out.println("EndTime: "+endTime);
	 		System.out.println("DiffTime: "+diffTime);
	 		System.out.println("DownloadSpeed for "+reqKey+ ", chunkSize: "+chunkSize+" :::"+ downloadSpeed);
	 		
	 		peerTrustFactorDetails.getDownloadSpeedList().add(downloadSpeed);
	 		
		}catch (Exception e){
			e.printStackTrace();
			
		}
		
	}
}
