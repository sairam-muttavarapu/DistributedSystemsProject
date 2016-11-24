package com.p2p.utils;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity 

public class TrustFactorDetails {
	
	@Id
	private String email;
	private String trustFactor;
	private String numTransactions;
	
	public TrustFactorDetails(){
		
	}

	public TrustFactorDetails(String email, String trustFactor, String numTransactions) {
		super();
		this.email = email;
		this.trustFactor = trustFactor;
		this.numTransactions = numTransactions;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTrustFactor() {
		return trustFactor;
	}
	public void setTrustFactor(String trustFactor) {
		this.trustFactor = trustFactor;
	}
	public String getNumTransactions() {
		return numTransactions;
	}
	public void setNumTransactions(String numTransactions) {
		this.numTransactions = numTransactions;
	}
}
