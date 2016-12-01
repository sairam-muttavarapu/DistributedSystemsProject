package com.p2p.dht;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Arrays;

import com.p2p.security.EncryptionUtil;
import com.p2p.security.SymmetricEncryption;
import com.p2p.utils.HTTPRequestResponseHandler;


public class FileServerThread implements Runnable{
	private Thread serverThread;
	private String threadName;
	
	public FileServerThread(String _threadName) {
		// TODO Auto-generated constructor stub
		threadName = _threadName;
		System.out.println("ThreadName: "+threadName);
	}
	
	public void start(){
		if(serverThread == null){
			serverThread = new Thread(this,  threadName);
			serverThread.start();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			System.out.println("Starting FileServer");
			ServerSocket serverSocket = new ServerSocket(4003);
			//serverSocket.setSoTimeout(10000);
			while(true){
				System.out.println("\nWaiting for socket client connection");
				Socket connSocket = serverSocket.accept();
				
				System.out.println("Accepted socket client connection from "+connSocket.getRemoteSocketAddress().toString());
				BufferedReader recvBuffReader = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
				String msgClient = recvBuffReader.readLine();
				//DataInputStream in = new DataInputStream(connSocket.getInputStream());
	            //System.out.println(in.readUTF());
				
				
				System.out.println("Client requested: "+msgClient);
				
				//String[] dotSplitArr = msgClient.split("_")[0].split(".");
				//String fileExt = dotSplitArr[dotSplitArr.length-1];
				
				File file = new File("./share/"+msgClient.split("_")[0]);
				
				
				long fileSizeToUpload = file.length();
				
				if(fileSizeToUpload > P2PControllerClientPeer.FILE_ZERO_KB && fileSizeToUpload <= P2PControllerClientPeer.FILE_ONE_MB){
					P2PControllerClientPeer.CHUNK_SIZE_UPLOAD = P2PControllerClientPeer.CHUNK_ONE_KB;
	     		}else if(fileSizeToUpload > P2PControllerClientPeer.FILE_ONE_MB && fileSizeToUpload <= P2PControllerClientPeer.FILE_HUNDRED_MB){
	     			P2PControllerClientPeer.CHUNK_SIZE_UPLOAD = P2PControllerClientPeer.CHUNK_ONE_MB;
	     		}else if(fileSizeToUpload > P2PControllerClientPeer.FILE_HUNDRED_MB && fileSizeToUpload <= P2PControllerClientPeer.FILE_ONE_GB){
	     			P2PControllerClientPeer.CHUNK_SIZE_UPLOAD = P2PControllerClientPeer.CHUNK_FIFTY_MB;
	     		}else if(fileSizeToUpload > P2PControllerClientPeer.FILE_ONE_GB){
	     			P2PControllerClientPeer.CHUNK_SIZE_UPLOAD = P2PControllerClientPeer.CHUNK_HUNDRED_MB;
	     		}
				
				long chunkSize = P2PControllerClientPeer.CHUNK_SIZE_UPLOAD;
				
				
				
				/*if(fileExt.equalsIgnoreCase("jpg") || fileExt.equalsIgnoreCase("png")){
					BufferedImage image = ImageIO.read(file);
					
				}*/
	            
				String response="";

				DataOutputStream dataOutputStream = new DataOutputStream(connSocket.getOutputStream());
				FileInputStream fis = new FileInputStream(file);
				
				if(msgClient.contains("_Size")){
					response = fileSizeToUpload+"";
					System.out.println("Responding to the client with fileSize...");
					dataOutputStream.writeBytes(response);
					
				}else if(msgClient.contains("_Part")){
					//FileReader reads text files in the default encoding.
    				
					String reqParams = "queryType=get&service=QueryPubKey&ipaddress="+connSocket.getInetAddress().getHostAddress();
					String resultsStr = HTTPRequestResponseHandler.doHTTPPostRequest(reqParams);
					String statusStr = resultsStr.split("_")[0];
					
					String publicKey = "";
					if(statusStr.equalsIgnoreCase("Success")){

						if(resultsStr.split("_").length == 2){
							publicKey = resultsStr.split("_")[1];
							//System.out.println("Retrieved publicKey for the seeder: "+publicKey);
						}
						
					}else if(statusStr.equalsIgnoreCase("Failure")){
						System.out.println("Unable to retrieve publicKey from the server");
					}else if(statusStr.equalsIgnoreCase("Error")){
						System.out.println("Unknown Error Occurred. Try after sometime");
					}
					
					PublicKey pubKey = EncryptionUtil.getPublicKey(publicKey);
					
					//final String AESKey = EncryptionUtil.randomAESKeyGenerator();
					final String AESKey = "Mary has one cat";
					System.out.println("AESKey being sent: "+AESKey);
					//Encrypting AESKey using public key of the downloader
					final byte[] encryptedAESKey = EncryptionUtil.encrypt(AESKey, pubKey);
					
					dataOutputStream.write(encryptedAESKey); // Sending encryptedAESKey to the downloading peer
					
					int numReadBytes = 0;
					byte[] ackBytes = new byte[5];
					String ackStr = recvBuffReader.readLine();
					
					/*if((numReadBytes = recvBuffReader.read(ackBytes)) != -1){
						String str = new String(ackBytes, "UTF-8"); // for UTF-8 encoding
						ackStr = str;
					}*/
					
					System.out.println("ackStr received: "+ackStr);
					if(ackStr.startsWith("Ok")){
						System.out.println("ACK came for encrypted AES Key");
					}
					
    				int partNumber = Integer.parseInt(msgClient.split("Part")[1]);
    				// Wrapping FileReader in BufferedReader.
    				
    				System.out.println("FileServerThread FileSize: "+file.length());
    				
    				//P2PControllerClientPeer.CHUNK_SIZE_UPLOAD = file.length()/(long)1000;
    				
    				long fileSizeUploaded = partNumber*P2PControllerClientPeer.CHUNK_SIZE_UPLOAD;
     				long fileSizeRemaining = fileSizeToUpload - fileSizeUploaded;
     				System.out.println("Actual FileSize: "+ fileSizeToUpload);
     				System.out.println("FileSize Uploaded: "+ fileSizeUploaded);
     				
     				System.out.println("FileSize Remaining: "+ fileSizeRemaining);
     				
     				// If fileSizeRemaining is greater than CHUNK_SIZE_UPLOAD, then retain CHUNK_SIZE_UPLOAD
     				// If it is the last chunk, fileSizeRemaining will be less than set CHUNK_SIZE_UPLOAD, so using fileSizeRemaining for the last chunk
     				chunkSize = (fileSizeRemaining > P2PControllerClientPeer.CHUNK_SIZE_UPLOAD) ? (P2PControllerClientPeer.CHUNK_SIZE_UPLOAD): fileSizeRemaining; 
     				System.out.println("chunkSize: "+ chunkSize);
     				
      				boolean enableAESPadding = false;
      				if(chunkSize != P2PControllerClientPeer.CHUNK_SIZE_UPLOAD){
      					enableAESPadding = true;
      					double chunkSizeDivBy16 = chunkSize/16.0;
      					chunkSizeDivBy16 = Math.ceil(chunkSizeDivBy16);
      					chunkSize = 16*(int)chunkSizeDivBy16;
      				}
     				
     				fis.skip(partNumber*P2PControllerClientPeer.CHUNK_SIZE_UPLOAD); // skipping parts not requested, seeking to the part requested
     				
     				byte[] chunkSizeBytes = new byte[(int)chunkSize];
     				int numBytesRead = 0;
    				
    				if((numBytesRead = fis.read(chunkSizeBytes)) != -1){
    					
    		 			/*if(numBytesRead < chunkSize){
    		 				System.out.println("Inside small chunks, numBytesRead: "+numBytesRead);
    		 				byte[] chunkSizeBytesRead = Arrays.copyOf(chunkSizeBytes, numBytesRead);
    	    				dataOutputStream.write(chunkSizeBytesRead);
    	    				
    		 			}else{
    		 				System.out.println("Inside proper chunks, numBytesRead: "+numBytesRead);
    		 				dataOutputStream.write(chunkSizeBytes);
    		 			}*/
    		 			System.out.println("Inside chunks, numBytesRead: "+numBytesRead);
    		 			byte[] chunkSizeBytesRead = Arrays.copyOf(chunkSizeBytes, numBytesRead);
    		 			//String inStr = new String(chunkSizeBytesRead, "UTF-8"); // for UTF-8 encoding
    		 			//System.out.println("inStr: "+inStr);
    		 			
    		 			byte[] outputBytes = new byte[chunkSizeBytesRead.length];
    		 			
    		 			// add encrypt call to encrypt the bytes read and write output bytes using outputstream onto socket
    		 			  
    		 			try{
    		 				//Symmetric.encrypt(originalText, inputFile, encryptedFile);
    		 				outputBytes = SymmetricEncryption.encrypt(AESKey, chunkSizeBytesRead, enableAESPadding);
    				    }catch (Exception ex) {
    				        System.out.println(ex.getMessage());
    				        ex.printStackTrace();
    				    }
    		 			
    		 			System.out.println("Output bytes length: "+ outputBytes.length);
    		 		//	String outStr = new String(outputBytes, "UTF-8"); // for UTF-8 encoding
    		 			//System.out.println("outStr: "+outStr);
    		 			//FileOutputStream foutStream = new FileOutputStream(new File("tmp_"+partNumber));
    		 			//foutStream.write(outputBytes);
	    				//dataOutputStream.write(chunkSizeBytesRead);
	    				dataOutputStream.write(outputBytes);

    				}
    				
    				//fis.close();
    				
    			
    				System.out.println("Responding to the client with filePart...");
				}else if(msgClient.contains("_md5sum")){
					Process p = Runtime.getRuntime().exec("md5sum "+file.getAbsolutePath());
             		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
             		response = in.readLine().split(" ")[0];
             		System.out.println("Responding to the client with md5sum...");
             		dataOutputStream.writeBytes(response);
				}
				//Thread.sleep(100);
				fis.close();
				connSocket.close();
				//System.out.println("Responded to the client, HelloClient");
				System.out.println("Successfully responded to the client\n");
				//serverThread.sleep(1000);
			}
			//serverSocket.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}	
	
}
