package com.p2p.ui;


import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import com.p2p.dht.*;
import com.p2p.utils.HTTPRequestResponseHandler;

public class FeedbackScreen {

	protected Shell shell;
	private Text txtFeedback;
	private static ArrayList<TrustFactorPlusIP> incomingArgs;
	private static Shell incomingShell;
	private static String status;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			FeedbackScreen window = new FeedbackScreen();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		if(incomingShell != null){
			incomingShell.setVisible(false);	
		}
		
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public static void updateIncomingShell(Shell _shell, ArrayList<TrustFactorPlusIP> _args, String _status){
		incomingShell = _shell;
		incomingArgs = _args;
		status = _status;
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		
		txtFeedback = new Text(shell, SWT.BORDER);
		txtFeedback.setBounds(113, 141, 81, 29);
		
		Button btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				for(TrustFactorPlusIP peer : incomingArgs){
					double trustfactor_local = (double)(peer.getTrustFactor() * 0.7) + (double)(Integer.parseInt(txtFeedback.getText()) * 0.3);
					
					 //Get the element (or) iterate through the ArrayList
                    String reqParams = "queryType=put&service=UpdateTrust&"+"email="+peer.getEmail()+
                                    "&trustFactor="+trustfactor_local+"&numTransactions="+peer.getNumTransactions();
                    String resultsStr = HTTPRequestResponseHandler.doHTTPPostRequest(reqParams);
                    String statusStr = resultsStr.split("_")[0];
                    if(statusStr.equalsIgnoreCase("Success")){
                          System.out.println("Succesfully updated trustFactor values");
                    }else if(statusStr.equalsIgnoreCase("Failure")){
                          System.out.println("TrustFactor update values failed");
                    }
				
				
				}
					
				
				//navigate back to homescreen
				String [] params = new String[2];
				HomeScreen.updateIncomingShell(shell, params);
				HomeScreen homeScreen = new HomeScreen();
				homeScreen.open();
			
			}
		});
		btnSubmit.setBounds(215, 141, 97, 29);
		btnSubmit.setText("Submit");
		
		Label lblFeedback = new Label(shell, SWT.NONE);
		lblFeedback.setAlignment(SWT.CENTER);
		lblFeedback.setBounds(52, 73, 343, 17);
		lblFeedback.setText("Your Feedback for the Download: 0-10");
		
		Label lblStatus = new Label(shell, SWT.NONE);
		lblStatus.setAlignment(SWT.CENTER);
		lblStatus.setBounds(188, 10, 71, 17);
		lblStatus.setText(status);

	}
}
