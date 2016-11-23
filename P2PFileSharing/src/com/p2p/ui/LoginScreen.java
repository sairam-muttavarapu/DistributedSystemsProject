package com.p2p.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class LoginScreen {
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
			LoginScreen window = new LoginScreen();
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
		
		Shell shlLogin = new Shell();
		shlLogin.setLocation(450,200);
		shlLogin.setSize(450, 300);
		shlLogin.setText("Login");
		
		Button btnLogin = new Button(shlLogin, SWT.NONE);
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String [] params = new String[2];
				params[0] = txtEmail.getText();
				HomeScreen.updateIncomingShell(shlLogin, params);
				HomeScreen homeScreen = new HomeScreen();
				homeScreen.open();
			}
		});
		btnLogin.setBounds(147, 134, 133, 25);
		btnLogin.setText("Login");
		
		Button btnSignUp = new Button(shlLogin, SWT.NONE);
		btnSignUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String [] params = new String[2];
				UserRegistrationScreen.updateIncomingShell(shlLogin, params);
				UserRegistrationScreen userRegistrationScreen = new UserRegistrationScreen();
				userRegistrationScreen.open();
			}
		});
		btnSignUp.setBounds(78, 179, 133, 25);
		btnSignUp.setText("Sign Up");
		
		Button btnForgotPassword = new Button(shlLogin, SWT.NONE);
		btnForgotPassword.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String [] params = new String[2];
				ForgotPasswordScreen.updateIncomingShell(shlLogin, params);
				ForgotPasswordScreen forgotPasswordScreen = new ForgotPasswordScreen();
				forgotPasswordScreen.open();
			}
		});
		btnForgotPassword.setBounds(225, 179, 133, 25);
		btnForgotPassword.setText("Forgot Password");
		
		txtEmail = new Text(shlLogin, SWT.BORDER);
		txtEmail.setBounds(200, 45, 145, 25);
		
		txtPassword = new Text(shlLogin, SWT.BORDER);
		txtPassword.setBounds(200, 83, 145, 25);
		
		CLabel lblEmailId = new CLabel(shlLogin, SWT.NONE);
		lblEmailId.setBounds(43, 45, 133, 25);
		lblEmailId.setText("Email Id");
		
		CLabel lblPassword = new CLabel(shlLogin, SWT.NONE);
		lblPassword.setText("Password");
		lblPassword.setBounds(43, 83, 133, 25);

		
		shlLogin.open();
		shlLogin.layout();
		while (!shlLogin.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
