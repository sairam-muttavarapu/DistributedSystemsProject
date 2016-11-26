package com.p2p.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.p2p.utils.EmailUtil;
import com.p2p.utils.HTTPRequestResponseHandler;
import com.p2p.utils.UserDetails;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Label;

public class UserRegistrationScreen {
	private Text txtFullname;
	private Text txtEmail;
	private Text txtPassword;
	private static Shell incomingShell;
	private static String [] incomingArgs;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UserRegistrationScreen window = new UserRegistrationScreen();
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
		Shell shlSignUp = new Shell();
		shlSignUp.setLocation(450,200);
		shlSignUp.setSize(450, 300);
		shlSignUp.setText("Sign Up");
		
		CLabel lblFullName = new CLabel(shlSignUp, SWT.NONE);
		lblFullName.setBounds(57, 35, 116, 21);
		lblFullName.setText("Full Name");
		
		txtFullname = new Text(shlSignUp, SWT.BORDER);
		txtFullname.setBounds(198, 35, 153, 21);
		
		CLabel lblEmail = new CLabel(shlSignUp, SWT.NONE);
		lblEmail.setText("Email");
		lblEmail.setBounds(57, 75, 116, 21);
		
		txtEmail = new Text(shlSignUp, SWT.BORDER);
		txtEmail.setBounds(198, 75, 153, 21);
		
		CLabel lblPassword = new CLabel(shlSignUp, SWT.NONE);
		lblPassword.setText("Password");
		lblPassword.setBounds(57, 116, 116, 21);
		
		txtPassword = new Text(shlSignUp, SWT.BORDER);
		txtPassword.setBounds(198, 116, 153, 21);
		txtPassword.setEchoChar('*');
		
		Label lblStatus = new Label(shlSignUp, SWT.NONE);
		Button btnSubmit = new Button(shlSignUp, SWT.NONE);
		
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
					
					/*
					UserDetails getUser = new UserDetails();
					UserDetails userDetails = new UserDetails(txtFullname.getText(), txtEmail.getText(), txtPassword.getText());
					SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
					Session session = sessionFactory.openSession();
					session.beginTransaction();
					
					getUser = session.get(com.p2p.utils.UserDetails.class, txtEmail.getText());
					if(getUser != null){
						System.out.println("User already exists");
						lblStatus.setText("User with given Email already exists");
					}else{
						
						session.save(userDetails);
						session.getTransaction().commit();
						
						getUser = session.get(com.p2p.utils.UserDetails.class, txtEmail.getText());
						System.out.println("getUser after adding the user: "+getUser);
					
						if(getUser != null){
							if(getUser.getName().equals(txtFullname.getText())){
								
								int status = EmailUtil.sendEmail(txtEmail.getText(), "SINT Welcome Email", "Dear "+txtFullname.getText().split(" ")[0]+",\n\nWelcome to SINT!\n\n"
										+ "You are now registered with SINT with these details\n"
										+ "Email: "+txtEmail.getText()+"\nFull Name: "+txtFullname.getText()+"\n\nUse your email id to login to SINT!\n\nCheers!\nTeam SINT");
								
								System.out.println("status: "+status);
								
								String [] params = new String[2];
								
								params[0] = txtEmail.getText();
								params[1] = txtFullname.getText();
								
								HomeScreen.updateIncomingShell(shlSignUp, params);
								HomeScreen homeScreen = new HomeScreen();
								homeScreen.open();
							}
						}
					}
					
					session.close();
					sessionFactory.close();
					*/
					
					String reqParams = "queryType=get&service=QueryUser&"+"email="+txtEmail.getText();
					String resultsStr = HTTPRequestResponseHandler.doHTTPPostRequest(reqParams);
					String statusStr = resultsStr.split("_")[0];
					
					if(statusStr.equalsIgnoreCase("Success")){
						System.out.println("User already exists");
						lblStatus.setText("User with given Email already exists");
					}else if(statusStr.equalsIgnoreCase("Failure")){
						lblStatus.setText("");
						reqParams = "queryType=put&service=AddUser&"+"email="+txtEmail.getText()+"&name="+txtFullname.getText()+"&password="+txtPassword.getText();
						resultsStr = HTTPRequestResponseHandler.doHTTPPostRequest(reqParams);
						
						if(resultsStr.equalsIgnoreCase("Success")){
							
							/*int status = EmailUtil.sendEmail(txtEmail.getText(), "SINT Welcome Email", "Dear "+txtFullname.getText().split(" ")[0]+",\n\nWelcome to SINT!\n\n"
									+ "You are now registered with SINT with these details\n"
									+ "Email: "+txtEmail.getText()+"\nFull Name: "+txtFullname.getText()+"\n\nUse your email id to login to SINT!\n\nCheers!\nTeam SINT");
							
							System.out.println("status: "+status);*/
							
							String [] params = new String[2];
							
							params[0] = txtEmail.getText();
							params[1] = txtFullname.getText();
							
							HomeScreen.updateIncomingShell(shlSignUp, params);
							HomeScreen homeScreen = new HomeScreen();
							homeScreen.open();
							
						}else if(resultsStr.equalsIgnoreCase("Failure")){
							System.out.println("Server error");
							lblStatus.setText("User not added or confirmation email not sent, try again later");
						}else if(statusStr.equalsIgnoreCase("Error")){
							lblStatus.setText("Unknown Error Occurred. Try after sometime");
						}
					}else if(statusStr.equalsIgnoreCase("Error")){
						lblStatus.setText("Unknown Error Occurred. Try after sometime");
					}
					
					
			}
		});
		btnSubmit.setBounds(227, 195, 82, 25);
		btnSubmit.setText("Submit");
		
		Button btnBack = new Button(shlSignUp, SWT.NONE);
		btnBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String [] params = new String[2];
				LoginScreen.updateIncomingShell(shlSignUp, params);
				LoginScreen loginScreen = new LoginScreen();
				loginScreen.open();
			}
		});
		btnBack.setBounds(127, 195, 75, 25);
		btnBack.setText("Back");
		

		lblStatus.setAlignment(SWT.CENTER);
		lblStatus.setBounds(67, 154, 284, 25);

		shlSignUp.open();
		shlSignUp.layout();
		while (!shlSignUp.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
