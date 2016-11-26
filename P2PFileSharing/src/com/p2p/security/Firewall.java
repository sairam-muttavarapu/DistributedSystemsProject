package com.p2p.security;

import java.lang.Runtime;
import java.io.*;
import java.lang.Thread;

public class Firewall{

	public static class myThread extends Thread{
        public void run(){
           String prevIp = null;
           while(true){
	            try(BufferedReader br = new BufferedReader(new FileReader("tcp_security_logs"))){
	              StringBuilder sb = new StringBuilder();
	              String line = br.readLine();
	              if (line != null){
		                String[] parts = line.split(":");
		                //System.out.println("now : "+parts[1]+"prev : "+ prevIp);
		                if (parts[1].equals(prevIp)== false){
			                  //block 
			                  System.out.println("Blocking " + parts[1]);
			                  Firewall.executeCommand("sudo iptables -A INPUT -p tcp --dport 4003 -s "+parts[1]+" -j DROP");
			                  Firewall.executeCommand("sudo iptables-save");
		                }
		                prevIp = parts[1];
	              }
	              
	              int singleSynFloodIndex = 0;
	              int randomSynFloodIndex = 0;
	              int ackFloodIndex = 0;
	              
	              line = br.readLine();
	              if(line != null){
	            	  singleSynFloodIndex = Integer.parseInt(line.split(":")[1]);  
	              }
	              	              
	              line = br.readLine();
	              if(line != null){
	            	  randomSynFloodIndex = Integer.parseInt(line.split(":")[1]);  
	              }
	              
	              line = br.readLine();
	              if(line != null){
	            	  ackFloodIndex = Integer.parseInt(line.split(":")[1]);
	              }
	              
	              
	              //System.out.println("singleSynFloodIndex: "+singleSynFloodIndex);
	              //System.out.println("randomSynFloodIndex: "+randomSynFloodIndex);
	              //System.out.println("ackFloodIndex: "+ackFloodIndex);
	              
	              if(singleSynFloodIndex > 300 || randomSynFloodIndex > 300){
	            	  System.out.println("Possible SYN Flooding on port 4003");
	              }
	              
	              if(ackFloodIndex > 4000){
	            	  System.out.println("Possible ACK Flooding on port 4003");
	              }
	              
	              
	              myThread.sleep(30000);
	              
	            }catch(Exception e){
            	  e.printStackTrace();
	            }
           }
        }
     }

	 public static void parseLogs(){
	      Thread obj = new myThread();
	      obj.start();
	
	 }  
	 
	public static int executeCommand(String cmd) throws Exception{
	      if (cmd.length() == 0)
	        return 0;
	      try{
		      Process p = Runtime.getRuntime().exec(cmd);
		      return 1;
	      }
	      catch(IOException e){
	        System.out.println("Exception in executeCommand: "+e.getMessage());
	        return 0;
	      }
	}
	
	
    public static int setRules(String[] ips) throws Exception{
         
         for (String ip: ips){
	           if (ip.length() == 0)
	             return 0;
	           try{
	             Process p = Runtime.getRuntime().exec("sudo iptables -A INPUT -p tcp --dport 4003 -s "+ip+" -j ACCEPT");
	           }
	           catch(IOException e){
	             System.out.println("Exception in setRules: "+e.getMessage());
	             return 0;
	           }
	           Thread.sleep(200);
         }
         
         try{
	           Process q = Runtime.getRuntime().exec("sudo iptables -A INPUT -p tcp --dport 4003 -j DROP");
	           q = Runtime.getRuntime().exec("sudo iptables-save");
	           return 1; // success
         }
         catch(IOException e){
	           System.out.println("Exception in setRules: "+e.getMessage());
	           return 0;
         }  
	}
}
