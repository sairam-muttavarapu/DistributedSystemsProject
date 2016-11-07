package tomP2P;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import net.tomp2p.storage.TrackerData;

public class ChunkThread implements Runnable{
	public Thread chunkThread;
	private String threadName;
	private TrackerData trackerData;
	private String reqKey;
	
	public ChunkThread(String _threadName, TrackerData _trackerData, String _reqKey) {
		// TODO Auto-generated constructor stub
		threadName = _threadName;
		trackerData = _trackerData;
		reqKey = _reqKey;
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
	 		
	 		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
	 		String filePart = bufferedReader.readLine();
	         
	        //System.out.println("Server says " + filePart);
	        
	 		client.close();
	 		//System.out.println("fileName from reqKey: " + reqKey.split("_")[0]);
	 		File folder = new File("./download/tmp_"+reqKey.split("_")[0]);
	 		if(!folder.exists()){
	 			folder.mkdir();
	 		}
	 		
	 		PrintWriter tmpFilePrintWriter = new PrintWriter("./download/tmp_"+reqKey.split("_")[0]+"/tmp_"+reqKey);
	 		//tmpFilePrintWriter.print(trackerData.getPeerAddress()); // added for debug purpose only to check the provider for the fileChunk
	 		tmpFilePrintWriter.print(filePart);
	 		tmpFilePrintWriter.close();
	 		
		}catch (Exception e){
			
		}
		
	}
}
