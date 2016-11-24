package com.p2p.utils;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity 

public class UserEmailIP {
	
	private String email;
	
	@Id
	private String ipaddress;
	
	public UserEmailIP(){
		
	}
	
	public UserEmailIP(String email, String ipaddress) {
		super();
		this.email = email;
		this.ipaddress = ipaddress;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
}
