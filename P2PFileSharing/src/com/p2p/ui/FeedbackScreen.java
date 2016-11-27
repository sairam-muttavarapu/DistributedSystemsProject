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

	protected Shell shlFeedback;
	private Text txtFeedback;
	private static ArrayList<TrustFactorPlusIP> incomingArgs;
	private static Shell incomingShell;
	private static String[] incomingStrArr;

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
		shlFeedback.open();
		shlFeedback.layout();
		while (!shlFeedback.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public static void updateIncomingShell(Shell _shell, ArrayList<TrustFactorPlusIP> _args, String[] _strArr){
		incomingShell = _shell;
		incomingArgs = _args;
		incomingStrArr = _strArr;
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlFeedback = new Shell();
		shlFeedback.setSize(450, 300);
		shlFeedback.setText("Feedback");
		
		txtFeedback = new Text(shlFeedback, SWT.BORDER);
		txtFeedback.setBounds(140, 141, 81, 29);
		
		Label lblStatus = new Label(shlFeedback, SWT.NONE);
		Label lblFeedback = new Label(shlFeedback, SWT.NONE);
		Label lblFeedbackstatus = new Label(shlFeedback, SWT.NONE);
		
		Button btnSubmit = new Button(shlFeedback, SWT.NONE);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				
				if(!txtFeedback.getText().equalsIgnoreCase("") && !txtFeedback.getText().equalsIgnoreCase(" ")){
					int intFeedback = Integer.parseInt(txtFeedback.getText());
					if(intFeedback < 0 || intFeedback > 10){
						lblFeedbackstatus.setText("Please give feedback in 0-10 range");
						return;
					}
				}
				
				for(TrustFactorPlusIP peer : incomingArgs){
					double trustfactor_local = 10;
					if(txtFeedback.getText().equalsIgnoreCase("") || txtFeedback.getText().equalsIgnoreCase(" ")){
						if(peer.isMd5sumStatus()){
							trustfactor_local = (double)(peer.getTrustFactor() * 0.9) + (double)(10 * 0.1); // 10 feedback weight
						}else{
							trustfactor_local = (double)(peer.getTrustFactor() * 0.9) + (double)(0 * 0.1);  // 0 feedback weight
						}
					}else{
						if(peer.isMd5sumStatus()){
							trustfactor_local = (double)(peer.getTrustFactor() * 0.9) + (double)(10 * 0.1);	// 10 feedback weight
						}else{
							// user's feedback weight
							trustfactor_local = (double)(peer.getTrustFactor() * 0.9) + (double)(Integer.parseInt(txtFeedback.getText()) * 0.1);
						}
					}
					
					System.out.println("TrustFactor after feedback: "+trustfactor_local);
					
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
				params[0] = "feedBackScreen";
				params[1] = incomingStrArr[1];
				
				HomeScreen.updateIncomingShell(shlFeedback, params);
				HomeScreen homeScreen = new HomeScreen();
				homeScreen.open();
			
			}
		});
		btnSubmit.setBounds(236, 141, 97, 29);
		btnSubmit.setText("Submit");
		
		lblFeedback.setAlignment(SWT.CENTER);
		lblFeedback.setBounds(48, 90, 347, 29);
		lblFeedback.setText("Your Feedback for the Download: 0-10");
		
		lblStatus.setAlignment(SWT.CENTER);
		lblStatus.setBounds(38, 34, 347, 23);
		lblStatus.setText(incomingStrArr[0]);
		
		lblFeedbackstatus.setBounds(63, 188, 322, 29);

	}
}
