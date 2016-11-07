package tomP2P;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
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
import java.util.Random;
import java.util.Scanner;

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
public class ExampleSimple {

    final private Peer peer;
    private static final int CHUNK_SIZE = 1024;

    public ExampleSimple(int peerId) throws Exception {
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
        	
        	Data data = new Data("HelloWorld");
        	
			String curDir = System.getProperty("user.dir");
			System.out.println("Current Directory:"+curDir);
        	
        	String content = new Scanner(new File("testFile.txt")). useDelimiter("\\Z").next();
        	InputStream input = new FileInputStream(new File("testFile.txt"));
        	//OutputStream output;
        	
			//FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader("testFile.txt");
			// Wrapping FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);	
			char[] cbuf = new char[100];
			Arrays.fill(cbuf, '\0');
			//bufferedReader.read(cbuf, 0, 100);
			int i = 0;
			int numBytes = 0;
			int totalSize = 0;
			while((numBytes = bufferedReader.read(cbuf, 0, 100))!=-1){
				//System.out.println("Bytes Num:"+numBytes);
				//System.out.println("\nBytesRead:");
				//System.out.print(cbuf);
				String cbufStr = String.copyValueOf(cbuf,0,numBytes);
				//System.out.println(cbufStr);
				cbuf = new char[100];
				Arrays.fill(cbuf, '\0');
				Data data1 = new Data(cbufStr);
	        	Number160 nr1 = Number160.createHash("testFile.txt_Part"+i);
	        	FutureDHT futureDHT = peer.put(nr1).setData(data1).start();
	         	futureDHT.awaitUninterruptibly();
	         	i++;
	         	totalSize += numBytes;
			}
			
			Data data1 = new Data(totalSize+"");
			Number160 nr1 = Number160.createHash("testFile.txt_Size");
			FutureDHT futureDHT = peer.put(nr1).setData(data1).start();
         	futureDHT.awaitUninterruptibly();
         	
         	Process p = Runtime.getRuntime().exec("md5sum testFile.txt");
     		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
     		
     		String md5sum = in.readLine();
     		//System.out.println("md5sum: "+ md5sum);
         	data1 = new Data(md5sum);
			nr1 = Number160.createHash("testFile.txt_md5sum");
			futureDHT = peer.put(nr1).setData(data1).start();
         	futureDHT.awaitUninterruptibly();
         	

         	
        	//System.out.println("Content:"+content);
        	
        	Number160 nr = new Number160(testKey);
        	futureDHT = peer.put(nr).setData(data).start();
        	futureDHT.awaitUninterruptibly();
        	
        	//System.out.println("after await normal num");
        	
        	/*Data data1 = new Data(content);
        	Number160 nr1 = Number160.createHash("testFile.txt");
        	futureDHT = peer.put(nr1).setData(data1).start();
         	futureDHT.awaitUninterruptibly();*/
         	
         	
         	content = new Scanner(new File("testFile2.txt")).useDelimiter("\\Z").next();
        	//String content = new Scanner(new File(".\\testFile.txt")).useDelimiter("\\Z").next();
        	//System.out.println(content);
        	
        	Data data2 = new Data(content);
        	Number160 nr2 = Number160.createHash("testFile2.txt");
        	//System.out.println("111 testFile2.txt hash:"+nr2);
        	futureDHT = peer.put(nr2).setData(data2).start();
         	futureDHT.awaitUninterruptibly();
         	
        	//System.out.println("after await file");
        	
        	//System.out.println("futureDHT isSucess value:"+futureDHT.isSuccess()+"\n");
        	//while(futureDHT.getData().)
        	//futureDHT.getDataMap().keySet().iterator().next();
    		//futureDHT.getDataMap().values().iterator().next();
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
    		//System.out.println("Created otherPeer");
    		System.out.println("======================== Starting OtherPeer ========================");
    		//Random rnd1 = new Random();
        	//Peer peer = new PeerMaker(new Number160("0")).setPorts(4001).makeAndListen();
        	//System.out.println("BootstrapPeer:"+peer.getPeerID().toString());
    		
        	//PeerAddress peerAddress = new PeerAddress(new Number160(bootPeerId), InetAddress.getByName("elra-01.cs.colorado.edu"), 4001, 4001);
    		//PeerAddress peerAddress = new PeerAddress(new Number160(bootPeerId), InetAddress.getByName("NameNode1"), 4001, 4001);
    		PeerAddress peerAddress = new PeerAddress(Number160.createHash("1"), InetAddress.getByName("NameNode1"), 4001, 4001);
        	//PeerAddress peerAddress = new PeerAddress(new Number160(bootPeerId), InetAddress.getByName("Sairam"), 4001, 4001);
        	//System.out.println("NameNodeInet: "+ InetAddress.getByName("elra-01.cs.colorado.edu"));
        	//System.out.println("NameNodeInet: "+ InetAddress.getByName("NameNode1"));
        	//System.out.println("NameNodeInet: "+ InetAddress.getByName("Sairam"));
        	Peer another = new PeerMaker(Number160.createHash(args[1])).setPorts(4002).makeAndListen();
        	//Peer another = new PeerMaker(new Number160(otherPeerId)).setPorts(4002).makeAndListen();
        	System.out.println("OtherPeer:"+another.getPeerID().toString());
        	
        	FutureBootstrap future = another.bootstrap().setPeerAddress(peerAddress).start();
        	future.awaitUninterruptibly();
        	//System.out.println("future isSucess value:"+future.isSuccess());
        	//System.out.println("future getFailedReason value:"+future.getFailedReason());
        	//System.out.println("Afterbootstrapping:");
        	
        	/*FileServerThread fileServerThread = new FileServerThread("dataExchange");
        	fileServerThread.start();
        	Number160 nr = new Number160(testKey);
        	FutureDHT futureDHT = another.get(nr).start();
        	futureDHT.awaitUninterruptibly();
        	System.out.println("isSucess retrieved:"+futureDHT.isSuccess());
        	System.out.println("Value retrieved:"+futureDHT.getData().getObject().toString());/
        	

        	Number160 nr1;
        	/*if(args.length == 2){
        		 nr1 = Number160.createHash(args[1].toString());
        	}else{
        		 nr1 = Number160.createHash("testFile.txt");
        	}
        	
        	futureDHT = another.get(nr1).start();
        	futureDHT.awaitUninterruptibly();
        	if(futureDHT.isSuccess()){
        		PrintWriter po = new PrintWriter("testOut.txt");
        		System.out.println("isSucess retrieved:"+futureDHT.isSuccess());
            	System.out.println("Value retrieved:"+futureDHT.getData().getObject().toString());
        		//po.println(futureDHT.getData().getObject().toString());
        		po.print(futureDHT.getData().getObject().toString());
        		po.close();
        	}*/
        	/*PrintWriter po = new PrintWriter("testOut.txt");
        	for(int i =1; i<5;i++){
            	nr1 = Number160.createHash("testFile.txt_"+i);

             	futureDHT = another.get(nr1).start();
             	futureDHT.awaitUninterruptibly();
             	
             	if(futureDHT.isSuccess()){
             		
             		System.out.println("isSucess retrieved:"+futureDHT.isSuccess());
                 	System.out.println("Value retrieved:"+futureDHT.getData().getObject().toString());
             		//po.println(futureDHT.getData().getObject().toString());
             		po.print(futureDHT.getData().getObject().toString());
             		
             		
             	}
        	}
        	po.close();*/
        	
        	
        	System.out.println("====================== Welcome to P2P DHT System ====================== ");
        	System.out.println("=================  You can upload/download files now! ================= ");
        	
        	while(true){
        		
        		/*System.out.println("otherPeer knows: "+another.getPeerBean().getPeerMap().getAll());
        		Thread.sleep(2000);*/
        		
        		System.out.print("UserConsole: ");
        		String input = System.console().readLine();
        		//System.out.println(");
        		//get <fileName>, put <fileName>
        		String [] cmdArr = input.split(" ");
        		
        		Number160 nrSize;
        		Number160 nrmd5sum;
        		/*if(cmdArr.length == 2){
        			System.out.println("cmdArr[0]: "+cmdArr[0]);
            		System.out.println("cmdArr[1]: "+cmdArr[1]);	
        		}*/
        		
        		
        		if(cmdArr[0].equalsIgnoreCase("get")){
        			
        			System.out.println("Getting "+cmdArr[1]+"file...");
        			
        		 	
    	         	
    	         	/*FutureTracker futureTracker = another.getTracker(Number160.createHash(cmdArr[1])).start().awaitUninterruptibly();
    	         	if(futureTracker.isSuccess()){
    	         		System.out.println("\n\nRetrieved trackers: "+futureTracker.getTrackers()+ ", Size:" +futureTracker.getTrackers().size()+"\n\n");
	
    	         		TrackerData trackerdata = futureTracker.getTrackers().iterator().next();
    	         		System.out.println("\n\nRetrieved trackers[0]: "+trackerdata.getPeerAddress());
    	        
    	         		Socket client = new Socket(trackerdata.getPeerAddress().getInetAddress(), trackerdata.getPeerAddress().portUDP()+1);
    	         		DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
    	         		dataOutputStream.writeBytes(cmdArr[1]+"_Size");
    	         		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
    	         		String msgServer = bufferedReader.readLine();
    	         		System.out.println("FileSize Msg From Server: "+msgServer);
    	         		
    	         		client.close();
    	         		
    	         		//byte[] buf = (cmdArr[1]+"_Size").getBytes();	
    	         		//client.send(new DatagramPacket(buf, buf.length));
    	         		//System.out.println("Retrieved PeersOnTracker: "+futureTracker.getPeersOnTracker());
    	         		//another.createPeerConnection(trackerdata.getPeerAddress(), 10);
    	         		
    	         		
    	         	}else{
    	         		System.out.println("futureTracker not retrieved");
    	         	}*/
        			
    	         	
        			nrSize = Number160.createHash(cmdArr[1]+"_Size");
        			System.out.println("myPeerId: "+another.getPeerID());
        			//System.out.println("nrSize of "+cmdArr[1]+" file: "+nrSize);
        			
        			//System.out.println("XorDistance: "+another.getPeerID().xor(nrSize));
                 	FutureDHT futureDHT = another.get(nrSize).start();
                 	futureDHT.awaitUninterruptibly();
                 	
                 	if(futureDHT.isSuccess()){
                 		
                     	System.out.println("\nFileSize:"+futureDHT.getData().getObject().toString());
                     	System.out.println("RetrievedPeerId:"+futureDHT.getData().getPeerId().toString());
                     	//System.out.println("RetrievedXorDistance: "+futureDHT.getData().getPeerId().xor(nrSize)+"\n");
                     	
                 		Double fileSize = Double.parseDouble(futureDHT.getData().getObject().toString());
                 		
                     	nrmd5sum = Number160.createHash(cmdArr[1]+"_md5sum");
                     	futureDHT = another.get(nrmd5sum).start();
                     	futureDHT.awaitUninterruptibly();
                     	String md5sum = "";
                     	if(futureDHT.isSuccess()){
                     		System.out.println("\nFilemd5sum:"+futureDHT.getData().getObject().toString());
                     		System.out.println("RetrievedPeerId:"+futureDHT.getData().getPeerId().toString());
                         	//System.out.println("RetrievedXorDistance: "+futureDHT.getData().getPeerId().xor(nrmd5sum));
                     		
                     		md5sum = futureDHT.getData().getObject().toString();
                     		PrintWriter fileOut = new PrintWriter(".//download//"+cmdArr[1]);
                     		for(int i = 0; i <= (fileSize/CHUNK_SIZE); i++){
                     			System.out.println("Getting "+i+"th, chunk...");
                     			Number160 nrFilePart = Number160.createHash(cmdArr[1]+"_Part"+i);;
                             	futureDHT = another.get(nrFilePart).start();
                             	futureDHT.awaitUninterruptibly();
                             	if(futureDHT.isSuccess()){
                             		fileOut.print(futureDHT.getData().getObject().toString());
                             	}
                     		}
                     		fileOut.close();
                     	}
                     	
                     	if(!md5sum.equalsIgnoreCase("")){
                     		Process p = Runtime.getRuntime().exec("md5sum .//download//"+cmdArr[1]);
                     		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                     		
                     		if(md5sum.equalsIgnoreCase(in.readLine().split(" ")[0])){
                     			System.out.println("================== Checking DataIntegrity... PASS !!! ================== ");
                     		}else{
                     			System.out.println("===================== DataIntegrity compromised !!! ==================== ");
                     		}
                     	}
              
                 	}
        		}else if(cmdArr[0].equalsIgnoreCase("put")){
        			File folder = new File(".//share");
        			File[] listOffFiles = folder.listFiles();
        			System.out.println("Checking number of files in share folder...");
        			System.out.println("Number of files in share folder: "+listOffFiles.length);
        			for(int i=0; i<listOffFiles.length; i++){
        				//System.out.println("FileName: "+listOffFiles[i].getName()+", FileSize: "+listOffFiles[i].length());
        				System.out.println("\nUploading fileName: "+listOffFiles[i].getName());
        				System.out.println("Uploading fileSize: "+listOffFiles[i].length());
        				Data fileLengthVal = new Data(listOffFiles[i].length()+"");
        				Number160 fileLengthKey = Number160.createHash(listOffFiles[i].getName()+"_Size");
        				FutureDHT futureDHT = another.put(fileLengthKey).setData(fileLengthVal).start();
        	         	futureDHT.awaitUninterruptibly();
        	         	
        	         	
        	         	/*FutureTracker futureTracker = another.addTracker(Number160.createHash(listOffFiles[i].getName())).start().awaitUninterruptibly();
        	         	
        	         	if(futureTracker.isSuccess()){
        	         		System.out.println("Added "+listOffFiles[i].getName()+" as a tracker");
        	         	}
        	         	System.out.println("another.getPeerAddress():"+ another.getPeerAddress());*/
        	         	
        	         	//another.put(fileLengthKey).setData(new Data(another.getPeerAddress())).start().awaitUninterruptibly();
        	         	

        	         	//another.setDistributedTracker(distributedTracker);
        	         	//another.addTracker(Number160.createHash(listOffFiles[i].getName()))
        	         	
        	         	Process p = Runtime.getRuntime().exec("md5sum "+listOffFiles[i].getAbsolutePath());
        	     		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        	     		
        	     		String md5sum = in.readLine();
        	     		md5sum = md5sum.split(" ")[0];
        	     		//System.out.println("md5sum: "+ md5sum);
        	     		System.out.println("Uploading md5sum of file: "+md5sum);
        	         	Data md5sumVal = new Data(md5sum);
        	         	Number160 md5sumKey = Number160.createHash(listOffFiles[i].getName()+"_md5sum");
        				futureDHT = another.put(md5sumKey).setData(md5sumVal).start();
        				
        	         	futureDHT.awaitUninterruptibly();
        				 
        				//FileReader reads text files in the default encoding.
        				FileReader fileReader = new FileReader(listOffFiles[i].getAbsolutePath());
        				// Wrapping FileReader in BufferedReader.
        				BufferedReader bufferedReader = new BufferedReader(fileReader);	
        				char[] cbuf = new char[CHUNK_SIZE];
        				Arrays.fill(cbuf, '\0');
        				
        				int chunkIndex = 0;
        				int numBytes = 0;
        				while((numBytes = bufferedReader.read(cbuf, 0, CHUNK_SIZE))!=-1){
        					//System.out.println("Bytes Num:"+numBytes);
        					//System.out.println("\nBytesRead:");
        					System.out.println("Uploading "+chunkIndex+"th chunk...");
        					String cbufStr = String.copyValueOf(cbuf,0,numBytes);
        					//System.out.println(cbufStr);
        					cbuf = new char[CHUNK_SIZE];
        					Arrays.fill(cbuf, '\0');
        					Data filePartVal = new Data(cbufStr);
        					//System.out.println("\nIam hereeeee:");
        					Number160 filePartKey = Number160.createHash(listOffFiles[i].getName()+"_Part"+chunkIndex);
        		        	futureDHT = another.put(filePartKey).setData(filePartVal).start();
        		         	futureDHT.awaitUninterruptibly();
        		         	chunkIndex++;
        				}
        				
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
    	
    	//InetAddress inetAddr = new InetAddress ();
    	/*System.out.println("MyInetAddress:"+InetAddress.getLocalHost());
    	
    	System.out.println("MyInetAddress:"+InetAddress.getByName("NameNode1"));
    		
    	Random rnd1 = new Random();
    	Peer peer = new PeerMaker(new Number160(rnd1)).setPorts(4001).makeAndListen();
    	System.out.println("BootstrapPeer:"+peer.getPeerID().toString());
    	
    	Random rnd2 = new Random();
    	Peer another = new PeerMaker(new Number160(rnd2)).setPorts(4002).makeAndListen();
    	System.out.println("AnotherPeer:"+another.getPeerID().toString());
    	
    	FutureBootstrap future = another.bootstrap().setPeerAddress(peer.getPeerAddress()).start();
    	future.awaitUninterruptibly();
    	
    	System.out.println("Afterbootstrapping:");
    	
    	Data data = new Data("HelloWorld");
    	Number160 nr = new Number160(rnd1);
    	FutureDHT futureDHT = peer.put(nr).setData(data).start();
    	futureDHT.awaitUninterruptibly();
    	System.out.println("after await uninterruptabily");
    	futureDHT = another.get(nr).start();
    	futureDHT.awaitUninterruptibly();
    	System.out.println("Value retrieved:"+futureDHT.getData().getObject().toString());
    	peer.shutdown();
    	System.out.println("after peer shutdown");*/
    	
    	
    }

    private String get(String name) throws ClassNotFoundException, IOException {
        FutureDHT futureDHT = peer.get(Number160.createHash(name)).start();
        futureDHT.awaitUninterruptibly();
        if (futureDHT.isSuccess()) {
            return futureDHT.getData().getObject().toString();
        }
        return "not found";
    }

    private void store(String name, String ip) throws IOException {
        peer.put(Number160.createHash(name)).setData(new Data(ip)).start().awaitUninterruptibly();
    }
    
}



