package com.p2p.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequestResponseHandler {
	
	private final static String HTTP_USER_AGENT = "Mozilla/5.0";
	public static String doHTTPPostRequest(String postParameters){
		String results="";
		try {
			String requestURLStr = "http://35.164.30.142/webService.php";
			URL requestURL = new URL(requestURLStr);
			HttpURLConnection httpURLConn = (HttpURLConnection)requestURL.openConnection();
			
			httpURLConn.setRequestMethod("POST");
			httpURLConn.setRequestProperty("User-Agent", HTTP_USER_AGENT);
			//httpURLConn.setRequestProperty("", value);
			
			httpURLConn.setDoOutput(true);
			DataOutputStream httpDataOutputStream = new DataOutputStream(httpURLConn.getOutputStream());
			//postParameters = "email=a&password=a";
			httpDataOutputStream.writeBytes(postParameters);
			System.out.println("\nSending 'POST' request to URL : " + requestURLStr);
			System.out.println("Post parameters : " + postParameters);
			
			int responseCode=httpURLConn.getResponseCode();
			System.out.println("Response Code : " + responseCode);
			if(responseCode == 200){
				BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConn.getInputStream()));
				String inputLine;
				
				//StringBuffer response = new StringBuffer();
				String response = "";

				while ((inputLine = in.readLine()) != null) {
					//response.append(inputLine);
					response += inputLine;
				}
				in.close();

				//print result
				System.out.println("Results:"+response.toString()+"\n");
				results = response.toString();
			}else{
				System.out.println("ResponseCode: "+responseCode);
				results = "Error";
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
		
	}
}
