package com.p2p.dht;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import net.tomp2p.storage.TrackerData;

public class ChunkThread implements Runnable{
	public Thread chunkThread;
	private String threadName;
	private TrackerData trackerData;
	private String reqKey;
	private int chunkSize;
	
	public ChunkThread(String _threadName, TrackerData _trackerData, String _reqKey, int _chunkSize) {
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
	 		byte[] data = new byte[chunkSize];
	 		
	 		if(inputStream.read(data,0,chunkSize) != -1){
	 			
	 			//System.out.println("fileName from reqKey: " + reqKey.split("_")[0]);
		 		File folder = new File("./download/tmp_"+reqKey.split("_")[0]);
		 		if(!folder.exists()){
		 			folder.mkdir();
		 		}
		 		
		 		FileOutputStream outputStream = new FileOutputStream(new File("./download/tmp_"+reqKey.split("_")[0]+"/tmp_"+reqKey));
		 		outputStream.write(data);
		 		outputStream.close();
	 		}

	        //System.out.println("Server says ");
	 		client.close();
	 		
		}catch (Exception e){
			e.printStackTrace();
			
		}
		
	}
}
