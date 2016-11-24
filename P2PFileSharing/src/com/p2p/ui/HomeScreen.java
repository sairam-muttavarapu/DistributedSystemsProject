package com.p2p.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.p2p.dht.P2PControllerClientPeer;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class HomeScreen {
	private Text txtSearchfilename;
	private static Shell incomingShell;
	private static String [] incomingArgs;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			HomeScreen window = new HomeScreen();
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
	public void open()  {
		if(incomingShell != null){
			incomingShell.setVisible(false);	
		}
		
		try {
			P2PControllerClientPeer.MakePeer(incomingArgs[0]);
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Display display = Display.getDefault();
		Shell shlHome = new Shell();
		shlHome.setLocation(450,200);
		shlHome.setSize(450, 300);
		shlHome.setText("Home");
		
		Label lblStatus = new Label(shlHome, SWT.NONE);
		Label lblWelcomeuser = new Label(shlHome, SWT.NONE);
		
		Button btnDownload = new Button(shlHome, SWT.NONE);
		btnDownload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Search and Download Logic goes here
				try {
					if(txtSearchfilename.getText() != null && !txtSearchfilename.getText().equalsIgnoreCase("")){
						P2PControllerClientPeer.GetFile(txtSearchfilename.getText());
					}else{
						lblStatus.setText("Please give filename");
					}
					
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnDownload.setBounds(223, 59, 100, 25);
		btnDownload.setText("Download");
		
		Button btnSync = new Button(shlHome, SWT.NONE);
		btnSync.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Sync Share Folder Logic goes here
				P2PControllerClientPeer.ShareFolder();
			}
		});
		btnSync.setToolTipText("Sync Files in Share Folder");
		btnSync.setBounds(329, 59, 100, 25);
		btnSync.setText("Sync");
		
		txtSearchfilename = new Text(shlHome, SWT.BORDER);
		txtSearchfilename.setBounds(31, 60, 182, 25);
		
		lblWelcomeuser.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblWelcomeuser.setAlignment(SWT.CENTER);
		//lblWelcomeuser.setText("Welcome User");
		lblWelcomeuser.setText("Welcome "+incomingArgs[1]); // Setting lblWelcomeuser Label to Welcome <Fullname> retrieved from database
		
		lblWelcomeuser.setBounds(27, 10, 231, 31);
		
		Button btnLogout = new Button(shlHome, SWT.NONE);
		btnLogout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.exit(0);
				/*String [] params = new String[2];
				LoginScreen.updateIncomingShell(shlHome, params);
				LoginScreen loginScreen = new LoginScreen();
				loginScreen.open();*/
			}
		});
		btnLogout.setBounds(273, 10, 100, 25);
		btnLogout.setText("Logout");
		

		lblStatus.setAlignment(SWT.CENTER);
		lblStatus.setBounds(62, 102, 311, 15);

		shlHome.open();
		shlHome.layout();
		while (!shlHome.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		if(shlHome.isDisposed()){
			System.out.println("Shutting down Client");
			P2PControllerClientPeer.KillPeer();
		}
		
	}
	
	
}
