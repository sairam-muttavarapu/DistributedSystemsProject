package com.p2p.dht;

import java.util.ArrayList;

import net.tomp2p.storage.TrackerData;

public class TrustFactorPlusIP implements Comparable<TrustFactorPlusIP>{

	private double trustFactor;
	private int numTransactions;
	private String email;
	private TrackerData trackerData;
	private ArrayList<Double> downloadSpeedList;
	private boolean md5sumStatus = false;
	
	public TrustFactorPlusIP(double trustFactor, int numTransactions, String email, TrackerData trackerData,
			ArrayList<Double> downloadSpeedList, boolean _md5sumStatus) {
		super();
		this.trustFactor = trustFactor;
		this.numTransactions = numTransactions;
		this.email = email;
		this.trackerData = trackerData;
		this.downloadSpeedList = downloadSpeedList;
		this.md5sumStatus = _md5sumStatus;
	}
	
	@Override
	public int compareTo(TrustFactorPlusIP o) {
		// TODO Auto-generated method stub
		
		//return 0;
		return (this.getTrustFactor() < o.getTrustFactor() ? -1 : 
            (this.getTrustFactor() == o.getTrustFactor() ? 0 : 1)); 
	}

	public double getTrustFactor() {
		return trustFactor;
	}

	public void setTrustFactor(double trustFactor) {
		this.trustFactor = trustFactor;
	}

	public int getNumTransactions() {
		return numTransactions;
	}

	public void setNumTransactions(int numTransactions) {
		this.numTransactions = numTransactions;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TrackerData getTrackerData() {
		return trackerData;
	}

	public void setTrackerData(TrackerData trackerData) {
		this.trackerData = trackerData;
	}

	public ArrayList<Double> getDownloadSpeedList() {
		return downloadSpeedList;
	}

	public void setDownloadSpeedList(ArrayList<Double> downloadSpeedList) {
		this.downloadSpeedList = downloadSpeedList;
	}

	public boolean isMd5sumStatus() {
		return md5sumStatus;
	}

	public void setMd5sumStatus(boolean md5sumStatus) {
		this.md5sumStatus = md5sumStatus;
	}	
	
}
