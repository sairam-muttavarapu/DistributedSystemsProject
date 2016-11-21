package com.p2p.dht;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import ch.qos.logback.classic.Logger;
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.futures.FutureTracker;
import net.tomp2p.connection.PeerConnection;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.DistributedHashTable;
import net.tomp2p.p2p.DistributedTracker;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;
import net.tomp2p.storage.TrackerData;
public class Driver {

    final private Peer peer;
    public static final int CHUNK_SIZE = 1024;

    public Driver(int peerId) throws Exception {
        peer = new PeerMaker(Number160.createHash(peerId)).setPorts(4000 + peerId).makeAndListen();
        FutureBootstrap fb = peer.bootstrap().setBroadcast().setPorts(4001).start();
        fb.awaitUninterruptibly();
        if (fb.getBootstrapTo() != null) {
            peer.discover().setPeerAddress(fb.getBootstrapTo().iterator().next()).start().awaitUninterruptibly();
        }
    }

    public static void main(String[] args) throws NumberFormatException, Exception {
        
    	int bootPeerId = 0x01;
    	int otherPeerId = 0x02;
    	int testKey = 0x03;
    	int fileKey = 0x04;
    	
    	System.out.println("Args[0]:"+args[0].toString());
    	if(args[0].equalsIgnoreCase("bootPeer")){
    		
    		System.out.println("======================== Starting BootPeer ========================");
    		
    		Number160 br = Number160.createHash("1");
    		//Number160 br = new Number160(bootPeerId);
        	Peer peer = new PeerMaker(br).setPorts(4001).makeAndListen();
        	
        	System.out.println("BootstrapPeer:"+peer.getPeerID().toString());
        	//System.out.println("BootstrapPeer Hash:"+br);
        	        	
			String curDir = System.getProperty("user.dir");
			System.out.println("Current Directory:"+curDir);
        	
        	//String content = new Scanner(new File("testFile.txt")). useDelimiter("\\Z").next();
        	//InputStream input = new FileInputStream(new File("testFile.txt"));
        	//OutputStream output;
        	
         	while(true){
        		System.out.println("bootPeer knows: "+peer.getPeerBean().getPeerMap().getAll());
        		//peer.getDistributedHashMap();
        		/*System.out.println("bootPeer knows numOfNodes: "+peer.getPeerBean().getStatistics().getEstimatedNumberOfNodes());
        		System.out.println("bootPeer knows getMaxCreating: "+peer.getConfiguration().getMaxCreating());
        		System.out.println("bootPeer knows getMaxOpenConnection: "+peer.getConfiguration().getMaxOpenConnection());
        		System.out.println("bootPeer knows isLimitTracker: "+peer.getConfiguration().isLimitTracker());
        		System.out.println("bootPeer knows isLimitTracker: "+peer.getConfiguration().getDiscoverTimeoutSec());*/
        		Thread.sleep(2000);
        	}
        	//peer.shutdown();
        	
    	}else if(args[0].equalsIgnoreCase("otherPeer")){
    		//System.out.println("Inside otherPeer");
    		System.out.println("======================== Starting OtherPeer ========================");
    		
        	//PeerAddress peerAddress = new PeerAddress(new Number160(bootPeerId), InetAddress.getByName("elra-01.cs.colorado.edu"), 4001, 4001);
    		//PeerAddress peerAddress = new PeerAddress(new Number160(bootPeerId), InetAddress.getByName("NameNode1"), 4001, 4001);
    		PeerAddress peerAddress = new PeerAddress(Number160.createHash("1"), InetAddress.getByName("NameNode1"), 4001, 4001);
        	//PeerAddress peerAddress = new PeerAddress(new Number160(bootPeerId), InetAddress.getByName("Sairam"), 4001, 4001);
        	//System.out.println("NameNodeInet: "+ InetAddress.getByName("elra-01.cs.colorado.edu"));
        	//System.out.println("NameNodeInet: "+ InetAddress.getByName("elra-02.cs.colorado.edu"));
        	//System.out.println("NameNodeInet: "+ InetAddress.getByName("Sairam"));
        	Peer another = new PeerMaker(Number160.createHash(args[1])).setPorts(4002).makeAndListen();
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
        	
        	
        	while(true){
        		
        		/*System.out.println("otherPeer knows: "+another.getPeerBean().getPeerMap().getAll());
        		Thread.sleep(2000);*/
        		
        		System.out.print("UserConsole: ");
        		String input = System.console().readLine();
        		//System.out.println(");
        		//get <fileName>, put
        		String [] cmdArr = input.split(" ");
        		
        		Number160 nrSize;
        		Number160 nrmd5sum;
        		/*if(cmdArr.length == 2){
        			System.out.println("cmdArr[0]: "+cmdArr[0]);
            		System.out.println("cmdArr[1]: "+cmdArr[1]);	
        		}*/
        		
        		if(cmdArr[0].equalsIgnoreCase("get")){
        			
        			System.out.println("Getting "+cmdArr[1]+"file...");
    	         	
    	         	FutureTracker futureTracker = another.getTracker(Number160.createHash(cmdArr[1])).start().awaitUninterruptibly();
    	         	if(futureTracker.isSuccess()){
    	         		//System.out.println("\n\nRetrieved trackers: "+futureTracker.getTrackers()+ ", Size:" +futureTracker.getTrackers().size()+"\n\n");
    	         		System.out.println("\nRetrieved trackers: "+futureTracker.getTrackers()+"\n");
	
    	         		TrackerData trackerData = futureTracker.getTrackers().iterator().next();
    	         		//System.out.println("\n\nRetrieved trackers[0]: "+trackerData.getPeerAddress());
    	        
    	         		Socket client = new Socket(trackerData.getPeerAddress().getInetAddress(), trackerData.getPeerAddress().portUDP()+1);
    	         		DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
    	         		
    	         		dataOutputStream.writeBytes(cmdArr[1]+"_Size\n");
    	         		//dataOutputStream.writeBytes(cmdArr[1]+"_Size");
    	         		System.out.println("Retrieving fileSize from Peer: "+trackerData.getPeerAddress().getInetAddress()+" ...");
    	         		//System.out.println("Sent the fileName_Size");
    	         		
    	         		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
    	         		String fileSize = bufferedReader.readLine();

    	                System.out.println("Peer: "+ trackerData.getPeerAddress().getInetAddress() +"replied fileSize as " + fileSize);
    	               
    	         		//System.out.println("FileSize Msg From Server: "+msgServer);
    	         		client.close();
    	         		
    	         		int fileSizeToDownload = Integer.parseInt(fileSize);
    	         		
    	         		// Debug code to check iterating through available trackers for the file 
    	         		Iterator<TrackerData> iterator = futureTracker.getTrackers().iterator();
    	         		/*while(iterator.hasNext()){
    	         			System.out.println("PeerDetails: "+iterator.next().getPeerAddress());
    	         		}*/
    	         		
    	         		//another.
    	         		
    	         		int partNumber = 0;
    	         		ChunkThread [] chunkThread = new ChunkThread[(fileSizeToDownload/CHUNK_SIZE)+1];
    	         		
    	         		while(fileSizeToDownload > 0){
    	         			iterator = futureTracker.getTrackers().iterator();
    	         			while(iterator.hasNext() && fileSizeToDownload > 0){
    	         				trackerData = iterator.next();
    	         				
    	         				chunkThread[partNumber] = new ChunkThread("chunkThread", trackerData, cmdArr[1]+"_Part"+partNumber);
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
    	         		
    	         		File folder = new File("./download/tmp_"+cmdArr[1]);
            			File[] listOfFiles = folder.listFiles();
            		
            			//System.out.println("Number of files in download folder: "+listOfFiles.length);
            			PrintWriter combineFile = new PrintWriter("./download/"+cmdArr[1]);
            			for(int i=0; i<listOfFiles.length; i++){
            				//System.out.println("FileName: "+listOfFiles[i].getName()+", FileSize: "+listOfFiles[i].length());
            				combineFile.print(new Scanner(listOfFiles[i].getName()).useDelimiter("\\Z").next()); // \\Z is endoffile delimiter 
            				//listOfFiles[i].delete(); // Deleting the tmp file part after writing into the actual file 
            			}
            			combineFile.close();
            			//folder.delete();
            			System.out.println("\n================= All Parts joined, FILE DOWNLOADED SUCCESSFULLY to download folder ==============");
            			System.out.println("\nChecking MD5SUM FILE INTEGRITY CHECK...");
            			
            			
            			// Filling up the hashmap of md5sums
            			ConcurrentHashMap<PeerAddress, String> md5SumHashMap = new ConcurrentHashMap<PeerAddress, String>();
            			Md5SumChunkThread [] md5SumChunkThread = new Md5SumChunkThread[futureTracker.getTrackers().size()];
            			iterator = futureTracker.getTrackers().iterator();
            			
            			int peerNum = 0;
	         			while(iterator.hasNext()){
	         				trackerData = iterator.next();
	         				
	         				md5SumChunkThread[peerNum] = new Md5SumChunkThread("md5sumchunkThread", trackerData, cmdArr[1]+"_md5sum", md5SumHashMap);
	         				if(md5SumChunkThread[peerNum] != null)
	         				{
	         					md5SumChunkThread[peerNum].start();
	         				}
	         				peerNum++;
	         			}
	         			
	         			for(int i=0; i< futureTracker.getTrackers().size(); i++){
	         				md5SumChunkThread[i].Md5SumChunkThread.join();
    	         		}
	         			
	         			File file = new File("./download/wiki.txt");
            			
	         			//Iterating through hashmap to find if all the peers are valid
	         			String tmpMd5Sum = "";
	         			Process proc = Runtime.getRuntime().exec("md5sum wiki.txt"); //"+file.getAbsolutePath());
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
    	         		System.out.println("No file with FileName: "+cmdArr[1]+" with the participating peers");
    	         	}
        			
        		}else if(cmdArr[0].equalsIgnoreCase("put")){
        			
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
        	         	
        	         	//another.put(fileLengthKey).setData(new Data(another.getPeerAddress())).start().awaitUninterruptibly();
        			}
        			
        			if(listOfFiles.length == 0){
        				System.out.println("No files to share with the participating peers");
        			}
        			
        		}else if(cmdArr[0].equalsIgnoreCase("help")){
        			System.out.println("================== HELP ===================");
                	System.out.println("1. get <fileName.ext> - To download the file");
                	System.out.println("2. put - To sync the share folder");
                	System.out.println("3. help - To get this help menu!");
        		}else if(cmdArr[0].equalsIgnoreCase("quit")){
        			System.out.println("Inside quit command");
        			another.shutdown();
        			if(another.isShutdown()){
        				System.out.println("shutdown successful");
        				break;
        			}
        		}
        	}
           	//another.shutdown();
    	}	
    	
    }
    
}



