package com.p2p.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import com.p2p.utils.EmailUtil;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

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
		lblFullName.setBounds(37, 41, 116, 21);
		lblFullName.setText("Full Name");
		
		txtFullname = new Text(shlSignUp, SWT.BORDER);
		txtFullname.setBounds(178, 41, 153, 21);
		
		CLabel lblEmail = new CLabel(shlSignUp, SWT.NONE);
		lblEmail.setText("Email");
		lblEmail.setBounds(37, 81, 116, 21);
		
		txtEmail = new Text(shlSignUp, SWT.BORDER);
		txtEmail.setBounds(178, 81, 153, 21);
		
		CLabel lblPassword = new CLabel(shlSignUp, SWT.NONE);
		lblPassword.setText("Password");
		lblPassword.setBounds(37, 122, 116, 21);
		
		txtPassword = new Text(shlSignUp, SWT.BORDER);
		txtPassword.setBounds(178, 122, 153, 21);
		
		Button btnSubmit = new Button(shlSignUp, SWT.NONE);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				
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
		});
		btnSubmit.setBounds(220, 175, 82, 25);
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
		btnBack.setBounds(120, 175, 75, 25);
		btnBack.setText("Back");

		shlSignUp.open();
		shlSignUp.layout();
		while (!shlSignUp.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
