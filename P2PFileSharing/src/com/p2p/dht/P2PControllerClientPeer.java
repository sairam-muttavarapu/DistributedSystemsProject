package com.p2p.dht;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.p2p.security.FirewallController;
import com.p2p.utils.HTTPRequestResponseHandler;
import com.p2p.utils.TrustFactorDetails;
import com.p2p.utils.UserDetails;
import com.p2p.utils.UserEmailIP;

import net.tomp2p.futures.FutureTracker;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.TrackerData;
public class P2PControllerClientPeer{

    private static Peer another;
    public static long CHUNK_SIZE_DOWNLOAD = 1024;
    public static long CHUNK_SIZE_UPLOAD = 1024;
    
    public static final long FILE_ZERO_KB = 0;
    public static final long FILE_ONE_MB = 1*1024*1024;
    public static final long FILE_HUNDRED_MB = 100*1024*1024;
    public static final long FILE_ONE_GB = 1000*1024*1024;
    
    public static final long CHUNK_ONE_KB = 1*1024;
    public static final long CHUNK_ONE_MB = 1*1024*1024;
    public static final long CHUNK_FIFTY_MB = 50*1024*1024;
    public static final long CHUNK_HUNDRED_MB = 50*1024*1024;


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
     		
     		Long.parseLong(fileSize);
     		long fileSizeToDownload = Long.parseLong(fileSize);
     		
     		
     		long copyfileSizeToDownload = fileSizeToDownload;
     		long chunkSize = CHUNK_SIZE_DOWNLOAD;
     		
     		
     		// Debug code to check iterating through available trackers for the file 
     		Iterator<TrackerData> iterator = futureTracker.getTrackers().iterator();
     		/*while(iterator.hasNext()){
     			System.out.println("PeerDetails: "+iterator.next().getPeerAddress());
     		}*/
     		
     		//another.
     		
     		if(fileSizeToDownload > FILE_ZERO_KB && fileSizeToDownload <= FILE_ONE_MB){
     			CHUNK_SIZE_DOWNLOAD = CHUNK_ONE_KB;
     		}else if(fileSizeToDownload > FILE_ONE_MB && fileSizeToDownload <= FILE_HUNDRED_MB){
     			CHUNK_SIZE_DOWNLOAD = CHUNK_ONE_MB;
     		}else if(fileSizeToDownload > FILE_HUNDRED_MB && fileSizeToDownload <= FILE_ONE_GB){
     			CHUNK_SIZE_DOWNLOAD = CHUNK_FIFTY_MB;
     		}else if(fileSizeToDownload > FILE_ONE_GB){
     			CHUNK_SIZE_DOWNLOAD = CHUNK_HUNDRED_MB;
     		}
     		
     		
     		int partNumber = 0;
     		
     		int numOfChunksToDownload = (int)((fileSizeToDownload/CHUNK_SIZE_DOWNLOAD)+1);
     		
     		ChunkThread [] chunkThread = new ChunkThread[numOfChunksToDownload];
     		long [] chunkSizeArray = new long[numOfChunksToDownload];
     		
     		System.out.println("ClientPeer: fileSizeToDownload: "+ fileSizeToDownload);
     		System.out.println("ClientPeer: CHUNK_SIZE_DOWNLOAD: "+ CHUNK_SIZE_DOWNLOAD);
     		System.out.println("ClientPeer: no. of chunks: "+ ((fileSizeToDownload/CHUNK_SIZE_DOWNLOAD)+1));
     		System.out.println("ClientPeer: no. of chunks(with int typecast): "+ numOfChunksToDownload);
     		
     	   /*//int numThreads = 0;
     	   //int partNum = 0;
     		while(fileSizeToDownload > 0){
     			iterator = futureTracker.getTrackers().iterator();
     			while(iterator.hasNext() && fileSizeToDownload > 0){
     				
     				/*if(numThreads == 100){
     					while(partNum < partNumber){
     		     			chunkThread[partNum].chunkThread.join();
     		     			//System.out.println("\npartNum: "+ partNum+"\n");
     		     			partNum++;
     		     		}
     					numThreads = 0;
     				}//
     				
     				trackerData = iterator.next();
     				long fileSizeDownloaded = partNumber*CHUNK_SIZE_DOWNLOAD;
     				long fileSizeRemaining = copyfileSizeToDownload - fileSizeDownloaded;
     				System.out.println(partNumber+" Actual FileSize: "+ copyfileSizeToDownload);
     				System.out.println(partNumber+" FileSize Downloaded: "+ fileSizeDownloaded);
     				
     				System.out.println(partNumber+" FileSize Remaining: "+ fileSizeRemaining);
     				
     				// If fileSizeRemaining is greater than CHUNK_SIZE_DOWNLOAD, then retain CHUNK_SIZE_DOWNLOAD
     				// If it is the last chunk, fileSizeRemaining will be less than set CHUNK_SIZE_DOWNLOAD, so using fileSizeRemaining for the last chunk
     				chunkSize = (fileSizeRemaining > CHUNK_SIZE_DOWNLOAD) ? (CHUNK_SIZE_DOWNLOAD): fileSizeRemaining;
     				
     				System.out.println(partNumber+" ClientPeer: ChunkSize: "+ chunkSize);
     				chunkSizeArray[partNumber] = chunkSize;
     				
     				chunkThread[partNumber] = new ChunkThread("chunkThread", trackerData, searchFileName+"_Part"+partNumber, chunkSize);
     				if(chunkThread[partNumber] != null)
     				{
     					chunkThread[partNumber].start();
     				}
     				fileSizeToDownload -= chunkSize;
     				partNumber++;
     				//numThreads++;
     			}
     		}
     		
     		partNumber = 0;
     		while(partNumber < numOfChunksToDownload){
     			chunkThread[partNumber].chunkThread.join();
     			//System.out.println("\npartNum: "+ partNum+"\n");
     			partNumber++;
     		}*/
     		
     		//Session Begin
     		/*SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
			Session session = sessionFactory.openSession();
			session.beginTransaction();*/
			
     		iterator = futureTracker.getTrackers().iterator();
     		int numOfFilePeers = futureTracker.getTrackers().size();
     		
     		ArrayList<TrustFactorPlusIP> trustFactorPlusIPArrayList = new ArrayList<TrustFactorPlusIP>();
     		
  			while(iterator.hasNext() && fileSizeToDownload > 0){
  				trackerData = iterator.next();
				String ipaddress = trackerData.getPeerAddress().getInetAddress().toString().split("/")[1];
				System.out.println("ipaddress plain: "+trackerData.getPeerAddress().getInetAddress().toString());
				System.out.println("ipaddress 0: "+trackerData.getPeerAddress().getInetAddress().toString().split("/")[0]);
				System.out.println("ipaddress 1: "+trackerData.getPeerAddress().getInetAddress().toString().split("/")[1]);
				System.out.println("ipaddress retrieved: "+ipaddress);
				/*UserEmailIP getUserEmailIP = new UserEmailIP();
				getUserEmailIP = session.get(com.p2p.utils.UserEmailIP.class, ipaddress);
				String emailId = getUserEmailIP.getEmail();
				
				TrustFactorDetails getTrustFactorDetails = new TrustFactorDetails();
				getTrustFactorDetails = session.get(com.p2p.utils.TrustFactorDetails.class, emailId);
				
				int trustFactor = Integer.parseInt(getTrustFactorDetails.getTrustFactor());
				int numTransactions = Integer.parseInt(getTrustFactorDetails.getNumTransactions());*/
				
				String reqParams = "queryType=get&service=TrustDetails&"+"ipaddress="+ipaddress;
				String resultsStr = HTTPRequestResponseHandler.doHTTPPostRequest(reqParams);
				String statusStr = resultsStr.split("_")[0];
				String emailId = "";
				int trustFactor = 0;
				int numTransactions = 0;
				if(statusStr.equalsIgnoreCase("Success")){
					
					if(resultsStr.split("_").length == 4){
						emailId = resultsStr.split("_")[1];
						trustFactor = Integer.parseInt(resultsStr.split("_")[2]);
						numTransactions = Integer.parseInt(resultsStr.split("_")[3]);
					}
				}else if(statusStr.equalsIgnoreCase("Failure")){
					System.out.println("No Email mapped for this IP");
				}else if(statusStr.equalsIgnoreCase("Error")){
					System.out.println("Unknown error occurred");
				}
				
				
				ArrayList<Double> downloadSpeedList = new ArrayList<Double>();
				trustFactorPlusIPArrayList.add(new TrustFactorPlusIP(trustFactor, numTransactions, emailId, trackerData, downloadSpeedList));
  			}

  			//Session close
  			//session.close();
			//sessionFactory.close();
  			
  			Collections.sort(trustFactorPlusIPArrayList);
  			
  			ArrayList<TrustFactorPlusIP> bestPeersList = new ArrayList<TrustFactorPlusIP>();
  			ArrayList<TrustFactorPlusIP> goodPeersList = new ArrayList<TrustFactorPlusIP>();
  			ArrayList<TrustFactorPlusIP> badPeersList = new ArrayList<TrustFactorPlusIP>();
  			
  			for(TrustFactorPlusIP trustFactorPlusIP : trustFactorPlusIPArrayList){
  				if(trustFactorPlusIP.getTrustFactor() >= 7){
  					bestPeersList.add(trustFactorPlusIP);
  				}else if(trustFactorPlusIP.getTrustFactor() >=3 && trustFactorPlusIP.getTrustFactor() < 7 ){
  					goodPeersList.add(trustFactorPlusIP);
  				}else if(trustFactorPlusIP.getTrustFactor() > 1 && trustFactorPlusIP.getTrustFactor() < 3){
  					badPeersList.add(trustFactorPlusIP);
  				}
  			}
  			
  			
  			int numOfChunksFromBestPeers = (int)(0.6*numOfChunksToDownload);
	  		int numOfChunksFromGoodPeers = (int)(0.3*numOfChunksToDownload);
	  		int numOfChunksFromBadPeers = numOfChunksToDownload - numOfChunksFromBestPeers - numOfChunksFromGoodPeers;
  			long fileSizeDownloaded;
  			long fileSizeRemaining;
    		int numThreads = 0;
    		int partNum = 0;
    		
	  		
    		if(bestPeersList.size() != 0 && goodPeersList.size() == 0 && badPeersList.size() == 0){
  	  			//Only Best Peers there
    			numOfChunksFromBestPeers = numOfChunksToDownload;
  				numOfChunksFromGoodPeers = 0;
  				numOfChunksFromBadPeers = 0;
  			}else if(bestPeersList.size() == 0 && goodPeersList.size() != 0 && badPeersList.size() == 0){
  				// Only Good Peers there
  				numOfChunksFromBestPeers = 0;
  				numOfChunksFromGoodPeers = numOfChunksToDownload;
  				numOfChunksFromBadPeers = 0;
  			}else if(bestPeersList.size() == 0 && goodPeersList.size() == 0 && badPeersList.size() != 0){
  			    // Only Bad Peers there
  				numOfChunksFromBestPeers = 0;
  				numOfChunksFromGoodPeers = 0;
  				numOfChunksFromBadPeers = numOfChunksToDownload;
  			}else if(bestPeersList.size() == 0 && goodPeersList.size() != 0 && badPeersList.size() != 0){
  			    // Only Good, Bad Peers there
  				numOfChunksFromBestPeers = 0;
  				numOfChunksFromGoodPeers = (int)(0.9*numOfChunksToDownload);
  				numOfChunksFromBadPeers = numOfChunksToDownload - numOfChunksFromGoodPeers;
  			}else if(bestPeersList.size() != 0 && goodPeersList.size() == 0 && badPeersList.size() != 0){
  				// Only Best, Bad Peers there
  				numOfChunksFromBestPeers = (int)(0.9*numOfChunksToDownload);
  				numOfChunksFromGoodPeers = 0;
  				numOfChunksFromBadPeers = numOfChunksToDownload - numOfChunksFromBestPeers;
  			}else if(bestPeersList.size() != 0 && goodPeersList.size() != 0 && badPeersList.size() == 0){
  			   // Only Best, Good Peers there
  				numOfChunksFromBestPeers = (int)(0.6*numOfChunksToDownload);
  				numOfChunksFromGoodPeers = numOfChunksToDownload - numOfChunksFromBestPeers;
  				numOfChunksFromBadPeers = 0;
  			}else if(bestPeersList.size() != 0 && goodPeersList.size() != 0 && badPeersList.size() != 0){
  	  			//Best, Good, Bad peers are there
  				numOfChunksFromBestPeers = (int)(0.6*numOfChunksToDownload);
  		  		numOfChunksFromGoodPeers = (int)(0.3*numOfChunksToDownload);
  		  		numOfChunksFromBadPeers = numOfChunksToDownload - numOfChunksFromBestPeers - numOfChunksFromGoodPeers;
  			}
  			
    		
  			while(numOfChunksFromBestPeers > 0){
  				for(TrustFactorPlusIP bestPeer : bestPeersList){
  					System.out.println("I am a Best Peer");
  					
      				if(copyfileSizeToDownload > FILE_HUNDRED_MB){
      					if(numThreads == 3){
          					while(partNum < partNumber){
          		     			chunkThread[partNum].chunkThread.join();
          		     			//System.out.println("\npartNum: "+ partNum+"\n");
          		     			partNum++;
          		     		}
          					numThreads = 0;
          				}
      				}
  	  				
  					fileSizeDownloaded = partNumber*CHUNK_SIZE_DOWNLOAD;
      				fileSizeRemaining = copyfileSizeToDownload - fileSizeDownloaded;
      				System.out.println(partNumber+" Actual FileSize: "+ copyfileSizeToDownload);
      				System.out.println(partNumber+" FileSize Downloaded: "+ fileSizeDownloaded);
      				
      				System.out.println(partNumber+" FileSize Remaining: "+ fileSizeRemaining);
      				
      				// If fileSizeRemaining is greater than CHUNK_SIZE_DOWNLOAD, then retain CHUNK_SIZE_DOWNLOAD
      				// If it is the last chunk, fileSizeRemaining will be less than set CHUNK_SIZE_DOWNLOAD, so using fileSizeRemaining for the last chunk
      				chunkSize = (fileSizeRemaining > CHUNK_SIZE_DOWNLOAD) ? (CHUNK_SIZE_DOWNLOAD): fileSizeRemaining;
      				
      				System.out.println(partNumber+" ClientPeer: ChunkSize: "+ chunkSize);
      				chunkSizeArray[partNumber] = chunkSize;
      				
      				chunkThread[partNumber] = new ChunkThread("chunkThread", bestPeer.getTrackerData(), searchFileName+"_Part"+partNumber, chunkSize, bestPeer);
      				if(chunkThread[partNumber] != null)
      				{
      					chunkThread[partNumber].start();
      				}
      				
      				fileSizeToDownload -= chunkSize;
      				numThreads++;
  					partNumber++;
  					numOfChunksFromBestPeers--;
  	  			}
  			}
  			
  			
  			while(numOfChunksFromGoodPeers > 0){
  				for(TrustFactorPlusIP goodPeer : goodPeersList){
  					System.out.println("I am a Good Peer");
  					
      				if(copyfileSizeToDownload > FILE_HUNDRED_MB){
      					if(numThreads == 3){
          					while(partNum < partNumber){
          		     			chunkThread[partNum].chunkThread.join();
          		     			//System.out.println("\npartNum: "+ partNum+"\n");
          		     			partNum++;
          		     		}
          					numThreads = 0;
          				}
      				}
      				
  					
  					fileSizeDownloaded = partNumber*CHUNK_SIZE_DOWNLOAD;
      				fileSizeRemaining = copyfileSizeToDownload - fileSizeDownloaded;
      				System.out.println(partNumber+" Actual FileSize: "+ copyfileSizeToDownload);
      				System.out.println(partNumber+" FileSize Downloaded: "+ fileSizeDownloaded);
      				
      				System.out.println(partNumber+" FileSize Remaining: "+ fileSizeRemaining);
      				
      				// If fileSizeRemaining is greater than CHUNK_SIZE_DOWNLOAD, then retain CHUNK_SIZE_DOWNLOAD
      				// If it is the last chunk, fileSizeRemaining will be less than set CHUNK_SIZE_DOWNLOAD, so using fileSizeRemaining for the last chunk
      				chunkSize = (fileSizeRemaining > CHUNK_SIZE_DOWNLOAD) ? (CHUNK_SIZE_DOWNLOAD): fileSizeRemaining;
      				
      				System.out.println(partNumber+" ClientPeer: ChunkSize: "+ chunkSize);
      				chunkSizeArray[partNumber] = chunkSize;
      				
      				chunkThread[partNumber] = new ChunkThread("chunkThread", goodPeer.getTrackerData(), searchFileName+"_Part"+partNumber, chunkSize, goodPeer);
      				if(chunkThread[partNumber] != null)
      				{
      					chunkThread[partNumber].start();
      				}
      				fileSizeToDownload -= chunkSize;
      				numThreads++;
  					partNumber++;
  					numOfChunksFromGoodPeers--;
  	  			}
  			}

  			while(numOfChunksFromBadPeers > 0){
  				for(TrustFactorPlusIP badPeer : badPeersList){
  					System.out.println("I am a Bad Peer");
  	 				if(copyfileSizeToDownload > FILE_HUNDRED_MB){
      					if(numThreads == 3){
          					while(partNum < partNumber){
          		     			chunkThread[partNum].chunkThread.join();
          		     			//System.out.println("\npartNum: "+ partNum+"\n");
          		     			partNum++;
          		     		}
          					numThreads = 0;
          				}
      				}
  	 				
  					fileSizeDownloaded = partNumber*CHUNK_SIZE_DOWNLOAD;
      				fileSizeRemaining = copyfileSizeToDownload - fileSizeDownloaded;
      				System.out.println(partNumber+" Actual FileSize: "+ copyfileSizeToDownload);
      				System.out.println(partNumber+" FileSize Downloaded: "+ fileSizeDownloaded);
      				
      				System.out.println(partNumber+" FileSize Remaining: "+ fileSizeRemaining);
      				
      				// If fileSizeRemaining is greater than CHUNK_SIZE_DOWNLOAD, then retain CHUNK_SIZE_DOWNLOAD
      				// If it is the last chunk, fileSizeRemaining will be less than set CHUNK_SIZE_DOWNLOAD, so using fileSizeRemaining for the last chunk
      				chunkSize = (fileSizeRemaining > CHUNK_SIZE_DOWNLOAD) ? (CHUNK_SIZE_DOWNLOAD): fileSizeRemaining;
      				
      				System.out.println(partNumber+" ClientPeer: ChunkSize: "+ chunkSize);
      				chunkSizeArray[partNumber] = chunkSize;
      				
      				chunkThread[partNumber] = new ChunkThread("chunkThread", badPeer.getTrackerData(), searchFileName+"_Part"+partNumber, chunkSize, badPeer);
      				if(chunkThread[partNumber] != null)
      				{
      					chunkThread[partNumber].start();
      				}
      				fileSizeToDownload -= chunkSize;
      				numThreads++;
  					partNumber++;
  					numOfChunksFromBadPeers--;
  	  			}
  			}
     	
     		/*numThreads = 0;
      	    partNum = 0;
      		while(fileSizeToDownload > 0){
      			iterator = futureTracker.getTrackers().iterator();
      			while(iterator.hasNext() && fileSizeToDownload > 0){
      				
      				if(copyfileSizeToDownload > FILE_HUNDRED_MB){
      					if(numThreads == 3){
          					while(partNum < partNumber){
          		     			chunkThread[partNum].chunkThread.join();
          		     			//System.out.println("\npartNum: "+ partNum+"\n");
          		     			partNum++;
          		     		}
          					numThreads = 0;
          				}
      				}
      				
      				trackerData = iterator.next();
      				fileSizeDownloaded = partNumber*CHUNK_SIZE_DOWNLOAD;
      				fileSizeRemaining = copyfileSizeToDownload - fileSizeDownloaded;
      				System.out.println(partNumber+" Actual FileSize: "+ copyfileSizeToDownload);
      				System.out.println(partNumber+" FileSize Downloaded: "+ fileSizeDownloaded);
      				
      				System.out.println(partNumber+" FileSize Remaining: "+ fileSizeRemaining);
      				
      				// If fileSizeRemaining is greater than CHUNK_SIZE_DOWNLOAD, then retain CHUNK_SIZE_DOWNLOAD
      				// If it is the last chunk, fileSizeRemaining will be less than set CHUNK_SIZE_DOWNLOAD, so using fileSizeRemaining for the last chunk
      				chunkSize = (fileSizeRemaining > CHUNK_SIZE_DOWNLOAD) ? (CHUNK_SIZE_DOWNLOAD): fileSizeRemaining;
      				
      				System.out.println(partNumber+" ClientPeer: ChunkSize: "+ chunkSize);
      				chunkSizeArray[partNumber] = chunkSize;
      				
      				chunkThread[partNumber] = new ChunkThread("chunkThread", trackerData, searchFileName+"_Part"+partNumber, chunkSize);
      				if(chunkThread[partNumber] != null)
      				{
      					chunkThread[partNumber].start();
      				}
      				fileSizeToDownload -= chunkSize;
      				partNumber++;
      				numThreads++;
      			}
      		}*/
      		
      		partNum = 0;
      		while(partNum < numOfChunksToDownload){
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
			
			// Deleting the file to download if it already exists inside download folder
			File outputFile = new File("./download/"+searchFileName);
			if(outputFile.exists()){
				outputFile.delete();
			}
			
			FileOutputStream combineFile = new FileOutputStream("./download/"+searchFileName, true);
			for(int i=0; i<listOfFiles.length; i++){
				System.out.println("FileName: "+listOfFiles[i].getName()+", FileSize: "+listOfFiles[i].length());
				File tmpFile = new File("./download/tmp_"+searchFileName+"/tmp_"+searchFileName+"_Part"+i);
				
				FileInputStream inputStream = new FileInputStream(tmpFile);
				byte[] filePartBuf = new byte[(int)chunkSizeArray[i]];
				inputStream.read(filePartBuf, 0, (int)chunkSizeArray[i]);
				combineFile.write(filePartBuf);
				inputStream.close();
				
				tmpFile.delete(); // Deleting the tmp file part after writing into the actual file
				
			}
			combineFile.close();
			
			folder.delete(); // this will work only if the folder is empty
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
    	
    	String reqParams = "queryType=get&service=IPList";
		String resultsStr = HTTPRequestResponseHandler.doHTTPPostRequest(reqParams);
		String statusStr = resultsStr.split("_")[0];
    	if(statusStr.equalsIgnoreCase("Success")){
    		System.out.println("Starting DDOS Security Thread");
    		String [] resultsStrArray = resultsStr.split("_");
    		ArrayList<String> ipAddressToAllowList = new ArrayList<String>();
    		ipAddressToAllowList.add("35.164.30.142");
    		System.out.println("Allowing bootPeer to the iptables");
    		for(String results : resultsStrArray){
    			if(!results.equalsIgnoreCase("Success")){
    				ipAddressToAllowList.add(results);
    				System.out.println("Allowing "+results+" ipaddress to the iptables");
    			}
    		}
    		String[] ipAddressToAllowArr = ipAddressToAllowList.toArray(new String[ipAddressToAllowList.size()]);
    		FirewallController.Activate(ipAddressToAllowArr);
    		System.out.println("Succesfully started Firewall Security For Client Peer!");
    	}else if(statusStr.equalsIgnoreCase("Failure")){
    		System.out.println("Unable to retrieve IPs list, not starting DDOS Security Thread");
    	}else if(statusStr.equalsIgnoreCase("Error")){
    		System.out.println("Unknown error occurred");
    	}
    	
    	System.out.println("====================== Welcome to P2P DHT System ====================== ");
    	System.out.println("================== You can upload/download files now! ================= "); 	
    }
    
    public static void KillPeer(){
    	another.shutdown();
    }
    
}



