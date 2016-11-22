package com.p2p.dht;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import net.tomp2p.futures.FutureTracker;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.TrackerData;
public class P2PControllerClientPeer {

    private static Peer another;
    public static final int CHUNK_SIZE = 1024;


    public P2PControllerClientPeer(){
        System.out.println("Inside P2PControllerClientPeer Constructor");
    }

    public static void GetFile(String searchFileName) throws NumberFormatException, Exception{
    	
     	FutureTracker futureTracker = another.getTracker(Number160.createHash(searchFileName)).start().awaitUninterruptibly();
     	if(futureTracker.isSuccess()){
     		//System.out.println("\n\nRetrieved trackers: "+futureTracker.getTrackers()+ ", Size:" +futureTracker.getTrackers().size()+"\n\n");
     		System.out.println("\nRetrieved trackers: "+futureTracker.getTrackers()+"\n");

     		TrackerData trackerData = futureTracker.getTrackers().iterator().next();
     		//System.out.println("\n\nRetrieved trackers[0]: "+trackerData.getPeerAddress());
    
     		Socket client = new Socket(trackerData.getPeerAddress().getInetAddress(), trackerData.getPeerAddress().portUDP()+1);
     		DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
     		
     		dataOutputStream.writeBytes(searchFileName+"_Size\n");
     		//dataOutputStream.writeBytes(searchFileName+"_Size");
     		System.out.println("Retrieving fileSize from Peer: "+trackerData.getPeerAddress().getInetAddress()+" ...");
     		//System.out.println("Sent the fileName_Size");
     		
     		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
     		String fileSize = bufferedReader.readLine();

            System.out.println("Peer: "+ trackerData.getPeerAddress().getInetAddress() +"replied fileSize as " + fileSize);
           
     		//System.out.println("FileSize Msg From Server: "+msgServer);
     		client.close();
     		
     		int fileSizeToDownload = Integer.parseInt(fileSize);
     		int copyfileSizeToDownload = fileSizeToDownload;
     		int chunkSize = CHUNK_SIZE;
     		
     		
     		// Debug code to check iterating through available trackers for the file 
     		Iterator<TrackerData> iterator = futureTracker.getTrackers().iterator();
     		/*while(iterator.hasNext()){
     			System.out.println("PeerDetails: "+iterator.next().getPeerAddress());
     		}*/
     		
     		//another.
     		
     		int partNumber = 0;
     		ChunkThread [] chunkThread = new ChunkThread[(fileSizeToDownload/CHUNK_SIZE)+1];
     		int [] chunkSizeArray = new int[(fileSizeToDownload/CHUNK_SIZE)+1];
     		
     		while(fileSizeToDownload > 0){
     			iterator = futureTracker.getTrackers().iterator();
     			while(iterator.hasNext() && fileSizeToDownload > 0){
     				trackerData = iterator.next();
     				chunkSize = ((chunkSize = (copyfileSizeToDownload - (partNumber*CHUNK_SIZE))) > CHUNK_SIZE) ? (CHUNK_SIZE): chunkSize;
     				chunkSizeArray[partNumber] = chunkSize;
     				chunkThread[partNumber] = new ChunkThread("chunkThread", trackerData, searchFileName+"_Part"+partNumber, chunkSize);
     				if(chunkThread[partNumber] != null)
     				{
     					chunkThread[partNumber].start();
     				}
     				fileSizeToDownload -= CHUNK_SIZE;
     				partNumber++;
     			}
     		}
     		
     		int partNum = 0;
     		while(partNum < partNumber){
     			chunkThread[partNum].chunkThread.join();
     			//System.out.println("\npartNum: "+ partNum+"\n");
     			partNum++;
     		}
     		
     		System.out.println("\n================= Hurrah all parts downloaded ================= ");
     		System.out.println("\nJoining all parts and making your file...");
     		
     		File folder = new File("./download/tmp_"+searchFileName);
			File[] listOfFiles = folder.listFiles();
		
			System.out.println("Number of files in download folder: "+listOfFiles.length);
			//PrintWriter combineFile = new PrintWriter("./download/"+searchFileName);
			
			// Deleting the existing file
			File outputFile = new File("./download/"+searchFileName);
			if(outputFile.exists()){
				outputFile.delete();
			}
			
			FileOutputStream combineFile = new FileOutputStream("./download/"+searchFileName, true);
			for(int i=0; i<listOfFiles.length; i++){
				System.out.println("FileName: "+listOfFiles[i].getName()+", FileSize: "+listOfFiles[i].length());
				File tmpFile = new File("./download/tmp_"+searchFileName+"/tmp_"+searchFileName+"_Part"+i);
				FileInputStream inputStream = new FileInputStream(tmpFile);
				byte[] buf = new byte[chunkSizeArray[i]];
				inputStream.read(buf, 0, chunkSizeArray[i]);
				combineFile.write(buf);
				//tmpFile.delete(); // Deleting the tmp file part after writing into the actual file
				
			}
			combineFile.close();
			
			//folder.delete(); // this will work only if the folder is empty
			System.out.println("\n================= All Parts joined, FILE DOWNLOADED SUCCESSFULLY to download folder ==============");
			System.out.println("\nChecking MD5SUM FILE INTEGRITY CHECK...");
			
			
			// Filling up the hashmap of md5sums
			ConcurrentHashMap<PeerAddress, String> md5SumHashMap = new ConcurrentHashMap<PeerAddress, String>();
			Md5SumChunkThread [] md5SumChunkThread = new Md5SumChunkThread[futureTracker.getTrackers().size()];
			iterator = futureTracker.getTrackers().iterator();
			
			int peerNum = 0;
 			while(iterator.hasNext()){
 				trackerData = iterator.next();
 				
 				md5SumChunkThread[peerNum] = new Md5SumChunkThread("md5sumchunkThread", trackerData, searchFileName+"_md5sum", md5SumHashMap);
 				if(md5SumChunkThread[peerNum] != null)
 				{
 					md5SumChunkThread[peerNum].start();
 				}
 				peerNum++;
 			}
 			
 			for(int i=0; i< futureTracker.getTrackers().size(); i++){
 				md5SumChunkThread[i].Md5SumChunkThread.join();
     		}
 			
 			//File file = new File("./download/wiki.txt");
			
 			//Iterating through hashmap to find if all the peers are valid
 			String tmpMd5Sum = "";
 			Process proc = Runtime.getRuntime().exec("md5sum ./download/"+searchFileName); //"+file.getAbsolutePath());
     		BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
     		String curMd5Sum = in.readLine().split(" ")[0];
     		System.out.println("Downloaded File's md5sum: "+curMd5Sum);
     		
     		int md5SumCheckCount = 0;
     		for(PeerAddress p: md5SumHashMap.keySet()){
     			tmpMd5Sum = md5SumHashMap.get(p);
     			//System.out.println("File's tmpMd5Sum: "+tmpMd5Sum+" from p: "+p.getInetAddress());
     			if(curMd5Sum.equalsIgnoreCase(tmpMd5Sum)){
     				System.out.println("DATA INTEGRITY VERIFIED with Peer: "+p.getInetAddress());
     				md5SumCheckCount++;
     			}else{
     				System.out.println("DATA INTEGRITY NOT VERIFIED with Peer: "+p.getInetAddress());
     			}
     		}
     		System.out.println("md5SumCheckCount: "+md5SumCheckCount);
     		if(md5SumCheckCount == futureTracker.getTrackers().size()){
     			System.out.println("================ Checking DataIntegrity... PASS !!! ================ ");
     		}else{
     			System.out.println("================  DATAINTEGRITY COMPROMISED !!! ================ ");
     		}
     		
     	}else{
     		//System.out.println("futureTracker not retrieved");
     		System.out.println("No file with FileName: "+searchFileName+" with the participating peers");
     	}
    }
    
    public static void ShareFolder(){

		File folder = new File(".//share");
		File[] listOfFiles = folder.listFiles();
		System.out.println("Checking number of files in share folder...");
		System.out.println("Number of files in share folder: "+listOfFiles.length);
		
		for(int i=0; i<listOfFiles.length; i++){
			//System.out.println("FileName: "+listOfFiles[i].getName()+", FileSize: "+listOfFiles[i].length());
         	
         	FutureTracker futureTracker = another.addTracker(Number160.createHash(listOfFiles[i].getName())).start().awaitUninterruptibly();
         	System.out.println("\nAdding "+listOfFiles[i].getName()+" file to the DHT IndirectReferenceTracker...");
			//System.out.println("Uploading fileSize: "+listOfFiles[i].length());
         	if(futureTracker.isSuccess()){
         		System.out.println("Successfully added "+listOfFiles[i].getName()+" to the tracker !!!");
         	}
         	//System.out.println("another.getPeerAddress():"+ another.getPeerAddress());
       
		}
		
		if(listOfFiles.length == 0){
			System.out.println("No files to share with the participating peers");
		}
    }
    
    public static void MakePeer(String email) throws NumberFormatException, Exception {
        
		System.out.println("======================== Starting OtherPeer ========================");

		PeerAddress peerAddress = new PeerAddress(Number160.createHash("BootPeer"), InetAddress.getByName("BootPeer"), 4001, 4001);
    	another = new PeerMaker(Number160.createHash(email)).setPorts(4002).makeAndListen();
    	//Peer another = new PeerMaker(new Number160(otherPeerId)).setPorts(4002).makeAndListen();
    	System.out.println("OtherPeer:"+another.getPeerID().toString());
    	
    	FutureBootstrap future = another.bootstrap().setPeerAddress(peerAddress).start();
    	future.awaitUninterruptibly();
    	//System.out.println("future isSucess value:"+future.isSuccess());
    	//System.out.println("future getFailedReason value:"+future.getFailedReason());
    	//System.out.println("Afterbootstrapping:");
    	if(future.isSuccess()){
    		System.out.println("Successfully bootstrapped !!!");
    	}else{
    		System.out.println("Bootstrapping failed...");
    	}
    	
    	// FileServerThread which handles file requests
    	FileServerThread fileServerThread = new FileServerThread("dataExchange");
    	fileServerThread.start();
    	
    	System.out.println("====================== Welcome to P2P DHT System ====================== ");
    	System.out.println("================== You can upload/download files now! ================= "); 	
    }
    
    public static void KillPeer(){
    	another.shutdown();
    }
    
}



