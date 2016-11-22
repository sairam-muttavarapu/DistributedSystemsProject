package com.p2p.dht;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import javax.imageio.ImageIO;

import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

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
				int chunkSize = P2PControllerBootPeer.CHUNK_SIZE;
				
				
				/*if(fileExt.equalsIgnoreCase("jpg") || fileExt.equalsIgnoreCase("png")){
					BufferedImage image = ImageIO.read(file);
					
				}*/
	            
				String response="";

				DataOutputStream dataOutputStream = new DataOutputStream(connSocket.getOutputStream());
				
				if(msgClient.contains("_Size")){
					response = file.length()+"";
					System.out.println("Responding to the client with fileSize...");
					dataOutputStream.writeBytes(response);
					
				}else if(msgClient.contains("_Part")){
					//FileReader reads text files in the default encoding.
    				
    				int partNumber = Integer.parseInt(msgClient.split("Part")[1]);
    				// Wrapping FileReader in BufferedReader.
    				
    				FileInputStream fis = new FileInputStream(file);
    				
    				chunkSize = ((chunkSize = ((int)file.length() - (partNumber*P2PControllerBootPeer.CHUNK_SIZE))) > P2PControllerBootPeer.CHUNK_SIZE) ? (P2PControllerBootPeer.CHUNK_SIZE): chunkSize;
    				byte[] cbuf = new byte[chunkSize];
    				
    				fis.skip(partNumber*P2PControllerBootPeer.CHUNK_SIZE); // skipping parts not requested, seeking to the part requested
    				
    				if(fis.read(cbuf, 0, chunkSize) != -1){
    					dataOutputStream.write(cbuf);
    				}
    				
    			
    				System.out.println("Responding to the client with filePart...");
				}else if(msgClient.contains("_md5sum")){
					Process p = Runtime.getRuntime().exec("md5sum "+file.getAbsolutePath());
             		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
             		response = in.readLine().split(" ")[0];
             		System.out.println("Responding to the client with md5sum...");
             		dataOutputStream.writeBytes(response);
				}
				
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
