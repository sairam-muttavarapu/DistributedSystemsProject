package com.p2p.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;


import java.util.Random;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import com.p2p.utils.EmailUtil;
import com.p2p.utils.HTTPRequestResponseHandler;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ForgotPasswordScreen {
	private static Shell incomingShell;
	private static String [] incomingArgs;
	private Text txtEmailId;
	private Text txtSecurityCode;
	private Text txtNewPassword;
	private Text txtConfirmNewPassword;
	private static int passwordRecovery;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ForgotPasswordScreen window = new ForgotPasswordScreen();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateIncomingShell(Shell _shell, String[] _args){
		incomingShell = _shell;
		incomingArgs = _args;
	}

	/**
	 * Open the window.
	 */
	public void open() {
		if(incomingShell != null){
			incomingShell.setVisible(false);	
		}
		Display display = Display.getDefault();
		Shell shlForgotPassword = new Shell();
		shlForgotPassword.setLocation(450,200);
		shlForgotPassword.setSize(450, 300);
		shlForgotPassword.setText("Forgot Password");
		
		Label lblSecurityCode = new Label(shlForgotPassword, SWT.NONE);
		Label lblNewPassword = new Label(shlForgotPassword, SWT.NONE);
		Label lblConfirmNewPassword = new Label(shlForgotPassword, SWT.NONE);
		CLabel lblStatus = new CLabel(shlForgotPassword, SWT.NONE);
		Button btnSend = new Button(shlForgotPassword, SWT.NONE);
		Button btnConfirm = new Button(shlForgotPassword, SWT.NONE);
		
		txtEmailId = new Text(shlForgotPassword, SWT.BORDER);
		txtEmailId.setText("Email Id");
		txtEmailId.setToolTipText("Enter your Email Id");
		txtEmailId.setBounds(72, 29, 162, 23);
		
		txtSecurityCode = new Text(shlForgotPassword, SWT.BORDER);
		txtSecurityCode.setBounds(220, 86, 169, 21);
		
		lblSecurityCode.setBounds(41, 89, 169, 18);
		lblSecurityCode.setText("Security Code");
		
		lblNewPassword.setText("New Password");
		lblNewPassword.setBounds(41, 126, 169, 18);
		
		txtNewPassword = new Text(shlForgotPassword, SWT.BORDER);
		txtNewPassword.setBounds(220, 123, 169, 21);
		
		lblConfirmNewPassword.setText("Confirm New Password");
		lblConfirmNewPassword.setBounds(41, 164, 169, 18);
		
		txtConfirmNewPassword = new Text(shlForgotPassword, SWT.BORDER);
		txtConfirmNewPassword.setBounds(220, 161, 169, 21);
		
		lblSecurityCode.setVisible(false);
		txtSecurityCode.setVisible(false);
		
		lblNewPassword.setVisible(false);
		txtNewPassword.setVisible(false);
		txtNewPassword.setEchoChar('*');
		
		lblConfirmNewPassword.setVisible(false);
		txtConfirmNewPassword.setVisible(false);
		txtConfirmNewPassword.setEchoChar('*');
		
		btnConfirm.setVisible(false);
		
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// Send button click, send email with random generated code to the user's Email Id.
				try {
				  //Random Number Generator
				   /*Random rand = new Random();
				   passwordRecovery = rand.nextInt(999999)+100000;
				   
				   int status = EmailUtil.sendEmail(txtEmailId.getText(),  "SINT Password Recovery" , "Security Code to create your new password: "+passwordRecovery);
				   
				   if(status == 1){
					   lblStatus.setText("Please check your email for the security code.");
				   }else{
					   lblStatus.setText("Email could not be sent");
				   }*/
				
				   String reqParams = "queryType=put&service=ForgotPassword&"+"email="+txtEmailId.getText();
				   String resultsStr = HTTPRequestResponseHandler.doHTTPPostRequest(reqParams);
				   String statusStr = resultsStr.split("_")[0];
				   if(statusStr.equalsIgnoreCase("Success")){
					   lblStatus.setText("Please check your email for the security code.");
					   lblSecurityCode.setVisible(true);
					   txtSecurityCode.setVisible(true);
					   lblNewPassword.setVisible(true);
					   lblConfirmNewPassword.setVisible(true);
					   txtNewPassword.setVisible(true);
					   txtConfirmNewPassword.setVisible(true);
					   btnConfirm.setVisible(true);					 
					   System.out.println("message sent successfully");
				   }else if(statusStr.equalsIgnoreCase("Failure")){
					   lblStatus.setText("Email could not be sent");
				   }else if(statusStr.equalsIgnoreCase("EmailSentEarlier")){
					   lblStatus.setText("Email with security code is sent earlier.");
					   lblSecurityCode.setVisible(true);
					   txtSecurityCode.setVisible(true);
					   lblNewPassword.setVisible(true);
					   lblConfirmNewPassword.setVisible(true);
					   txtNewPassword.setVisible(true);
					   txtConfirmNewPassword.setVisible(true);
					   btnConfirm.setVisible(true);	
				   }else{
					   lblStatus.setText(statusStr);
				   }
				   	
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		btnSend.setBounds(254, 27, 75, 25);
		btnSend.setText("Send");

		btnConfirm.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// Confirm button click, validate the entered code by quering from mysql database
				System.out.println("Entered security code: "+txtSecurityCode.getText());
				
				
				/*if(txtSecurityCode.getText().equalsIgnoreCase(passwordRecovery+"")){
					System.out.println("Validated Successfully");
					
				}else{
					System.out.println("Invalid security code");
				}*/
				if(!txtSecurityCode.getText().equalsIgnoreCase("") && !txtSecurityCode.getText().equalsIgnoreCase(" ") &&
				   !txtNewPassword.getText().equalsIgnoreCase("") && !txtNewPassword.getText().equalsIgnoreCase(" ") &&
				   !txtConfirmNewPassword.getText().equalsIgnoreCase("") && !txtConfirmNewPassword.getText().equalsIgnoreCase(" ")){
					
					if(txtNewPassword.getText().equalsIgnoreCase(txtConfirmNewPassword.getText())){
						String reqParams = "queryType=get&service=QuerySecurityCode&"+"email="+txtEmailId.getText()+"&securityCode="+txtSecurityCode.getText()+"&newPassword="+txtNewPassword.getText();
					    String resultsStr = HTTPRequestResponseHandler.doHTTPPostRequest(reqParams);
					    String statusStr = resultsStr.split("_")[0];
					    if(statusStr.equalsIgnoreCase("Success")){
					    	
					    	String fullName = "User";
							if(resultsStr.split("_")[1] != null){
								fullName = resultsStr.split("_")[1];
							}
							
					    	String [] params = new String[2];
							
							params[0] = txtEmailId.getText();
							params[1] = fullName;
							
							HomeScreen.updateIncomingShell(shlForgotPassword, params);
							HomeScreen homeScreen = new HomeScreen();
							homeScreen.open();
							
					    }else if(statusStr.equalsIgnoreCase("Failure")){
					    	lblStatus.setText("Security code not validated");
					    }else if(statusStr.equalsIgnoreCase("Error")){
					    	lblStatus.setText("Unknown error/Request a new security code");
					    }
					}else{
						lblStatus.setText("NewPassword,ConfirmNewPassword doesn't match");
					}
				}else{
					lblStatus.setText("Please enter all the required fields");
				}
			    
			}
		});
		btnConfirm.setBounds(172, 205, 75, 25);
		btnConfirm.setText("Confirm");
		
		
		lblStatus.setAlignment(SWT.CENTER);
		lblStatus.setBounds(72, 58, 270, 18);
		lblStatus.setText("");
		
		Button btnBack = new Button(shlForgotPassword, SWT.NONE);
		btnBack.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String [] params = new String[2];
				LoginScreen.updateIncomingShell(shlForgotPassword, params);
				LoginScreen loginScreen = new LoginScreen();
				loginScreen.open();
			}
		});
		btnBack.setBounds(349, 27, 75, 25);
		btnBack.setText("Back");

		shlForgotPassword.open();
		shlForgotPassword.layout();
		while (!shlForgotPassword.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
