package com.p2p.dht;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

import net.tomp2p.storage.TrackerData;

public class ChunkThread implements Runnable{
	public Thread chunkThread;
	private String threadName;
	private TrackerData trackerData;
	private String reqKey;
	private long chunkSize;
	
	public ChunkThread(String _threadName, TrackerData _trackerData, String _reqKey, long _chunkSize) {
		// TODO Auto-generated constructor stub
		threadName = _threadName;
		trackerData = _trackerData;
		reqKey = _reqKey;
		chunkSize = _chunkSize;
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
			// TODO Auto-generated method stub
			Socket client = new Socket(trackerData.getPeerAddress().getInetAddress(), trackerData.getPeerAddress().portUDP()+1);
	 		DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
	 		
	 		dataOutputStream.writeBytes(reqKey+"\n");
	 		//dataOutputStream.writeBytes(cmdArr[1]+"_Size");
	 		//System.out.println("Sent the fileName_Part<i>");
	 		
	 		System.out.println("Requesting Peer: "+trackerData.getPeerAddress().getInetAddress()+" for "+reqKey);
	 		
	 		InputStream inputStream = client.getInputStream();
	 		
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
		 				outputStream.write(chunkSizeBytesRead);

			 		
			 		//System.out.println("Leaving if condition");
		 		}
	 			chunkSizeRemaining -= numBytesRead;
	 		}
	 		outputStream.close();
	 		//Thread.currentThread().destroy();

	        //System.out.println("Server says ");
	 		
	 		//Thread.sleep(100);
	 		//client.close();
	 		
		}catch (Exception e){
			e.printStackTrace();
			
		}
		
	}
}
