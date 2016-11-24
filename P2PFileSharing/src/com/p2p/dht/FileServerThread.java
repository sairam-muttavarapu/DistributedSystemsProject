package com.p2p.dht;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;


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
	    				dataOutputStream.write(chunkSizeBytesRead);

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
