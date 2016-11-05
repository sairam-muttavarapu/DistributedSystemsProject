package tomP2P;

import net.tomp2p.storage.TrackerData;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import net.tomp2p.peers.PeerAddress;

public class Md5SumChunkThread implements Runnable {
	
	private String threadName;
	private TrackerData trackerData;
	private String reqKey;
	public Thread Md5SumChunkThread;
	private ConcurrentHashMap <PeerAddress, String> md5SumHashMap;
	
	public Md5SumChunkThread(String _threadName, TrackerData _trackerData, String _reqKey, ConcurrentHashMap <PeerAddress, String> _md5sumHashMap) {
		// TODO Auto-generated constructor stub
		threadName = _threadName;
		trackerData = _trackerData;
		md5SumHashMap = _md5sumHashMap;
		reqKey = _reqKey;
		
		System.out.println("ThreadName: "+threadName);
		//System.out.println("ThreadName: "+threadName);
	}
	
	public void start(){
		if(Md5SumChunkThread == null){
			Md5SumChunkThread = new Thread(this,  threadName);
			Md5SumChunkThread.start();
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
	 		System.out.println("Sent the fileName_md5sum");
	 		
	 		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
	 		String fileMd5Sum = bufferedReader.readLine();
	         
	        System.out.println("Hashmap from Server " + fileMd5Sum);
	        client.close();
	        md5SumHashMap.put(trackerData.getPeerAddress(), fileMd5Sum);
	        
		}catch(Exception e){
			
		}
		
	}
}
