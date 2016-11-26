package com.p2p.security;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

import com.p2p.security.Firewall.myThread;

public class FirewallController{
	
	public static void Activate(String[] ipAddressToAllowArr) throws Exception{	
        System.out.println("Entering Activate method");
        Firewall.executeCommand("sudo iptables --flush");
        
       // String[] ips = {"1.1.1.1","2.2.2.2","127.0.0.1"};
        if (Firewall.setRules(ipAddressToAllowArr)==0){
        	System.out.println("Failed to set rules");
        }else{
        	System.out.println("Succesfully added rules");
        }
          

        String line = "";
        try{
        	Process p = Runtime.getRuntime().exec("sudo pgrep a.out");
        	BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        	line = input.readLine();
        }catch(Exception e){
        	
        }
        
        System.out.println("a.out pid: "+ line);
        

        if (Firewall.executeCommand("sudo kill -9 "+line)== 0){
        	System.out.println("Failed to kill previous packet sniffer");
        }else{
        	System.out.println("Killed previous packet sniffer");
        }
        
        if (Firewall.executeCommand("rm -rf tcp_security_logs")== 0){
        	System.out.println("Failed delete tcp_security_logs");
        }else{
        	System.out.println("Deleted previous tcp_security_logs");
        }
        
        //Thread.sleep(200);
        
        if (Firewall.executeCommand("sudo ./a.out")== 0){
        	System.out.println("Failed to start new packet sniffer");
        }else{
        	System.out.println("Started new packet sniffer");
        }
            
        Thread.sleep(350);
        
        Firewall.parseLogs(); 
	}
}
