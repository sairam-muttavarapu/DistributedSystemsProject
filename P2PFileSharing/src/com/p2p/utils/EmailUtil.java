package com.p2p.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

	
	public static int sendEmail(String emailId, String emailSubject, String emailBody){
		
		int status = 0;
		InputStream input;
		Properties prop = new Properties();
		try {
			input = new FileInputStream("emailConfig.properties");
			prop.load(input);

			System.out.println("EmailId:"+prop.getProperty("emailId"));
			
			Session session = Session.getDefaultInstance(prop,  
			   new javax.mail.Authenticator() {  
			   protected PasswordAuthentication getPasswordAuthentication() {  
			   return new PasswordAuthentication(prop.getProperty("emailId"),prop.getProperty("password"));//change accordingly  
			   }  
			  });  

		    //compose message   
		   MimeMessage message = new MimeMessage(session);  
		   message.setFrom(new InternetAddress(prop.getProperty("emailId")));//change accordingly  
		   
		   message.addRecipient(Message.RecipientType.TO,new InternetAddress(emailId));  
		   message.setSubject(emailSubject);  

		   message.setText(emailBody); 
		   
		   //send message  
		   Transport.send(message);
		   
		   status = 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			status = 0;
		}
	   return status;
	}
}
