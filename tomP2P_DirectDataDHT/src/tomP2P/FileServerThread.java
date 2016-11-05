package tomP2P;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServerThread implements Runnable{
	private Thread serverThread;
	private String threadName;
	
	public FileServerThread(String name) {
		// TODO Auto-generated constructor stub
		threadName = name;
		System.out.println("ThreadName: "+threadName);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			ServerSocket serverSocket = new ServerSocket(4003);
			while(true){
				Socket connSocket = serverSocket.accept();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
				DataOutputStream dataOutputStream = new DataOutputStream(connSocket.getOutputStream());
				String clientMsg = bufferedReader.readLine();
				dataOutputStream.writeBytes("");
			}
			
		}catch(Exception e){
			
		}
		
	}
	
	public void start(){
		if(serverThread == null){
			serverThread = new Thread(this,  threadName);
			serverThread.start();
		}
	}
	
}
