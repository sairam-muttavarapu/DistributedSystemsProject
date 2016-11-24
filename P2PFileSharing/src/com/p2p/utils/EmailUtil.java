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
			//input = new FileInputStream("emailConfig.Properties");
			//input = EmailUtil.class.getResourceAsStream("/emailConfig.Properties");
			input = EmailUtil.class.getResourceAsStream("/hibernate.cfg.xml");
			
			if(input == null){
				System.out.println("input file is not empty");
			}
			
			prop.setProperty("mail.smtp.host", "smtp.gmail.com");
			prop.setProperty("mail.smtp.socketFactory.port", "465");
			prop.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
			prop.setProperty("mail.smtp.auth","true");
			prop.setProperty("mail.smtp.port","465");
			prop.setProperty("emailId", "sint.devteam@gmail.com");
			prop.setProperty("password","@cu4distsys");
			
			//prop.load(input);

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
