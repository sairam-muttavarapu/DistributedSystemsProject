package com.p2p.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import com.p2p.dht.*;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.custom.CLabel; 

public class HomeScreen {
	private static Text txtFileName;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) throws NumberFormatException, Exception {
		
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(574, 336);
		shell.setText("SWT Application");
		
		txtFileName = new Text(shell, SWT.BORDER);
		txtFileName.setBounds(28, 35, 220, 23);
		
		CLabel lblStatus = new CLabel(shell, SWT.NONE);
		lblStatus.setAlignment(SWT.CENTER);
		
		Button btnSearch = new Button(shell, SWT.NONE);
		
		btnSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				try {
					if(txtFileName.getText() != null && !txtFileName.getText().equalsIgnoreCase("")){
						P2PControllerClientPeer.GetFile(txtFileName.getText());
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
		btnSearch.setBounds(254, 33, 81, 23);
		btnSearch.setText("Search");
		
		Button btnDownload = new Button(shell, SWT.NONE);
		btnDownload.setBounds(341, 33, 111, 25);
		btnDownload.setText("Download");
		
		Button btnSyncFiles = new Button(shell, SWT.NONE);
		btnSyncFiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				P2PControllerClientPeer.ShareFolder();
			}
		});
		btnSyncFiles.setToolTipText("Share files in \"share\" folder");
		btnSyncFiles.setBounds(458, 34, 90, 23);
		btnSyncFiles.setText("Sync Files");
		

		lblStatus.setBounds(102, 250, 417, 23);
		lblStatus.setText("Status");
		System.out.println("About to start 123123");
		if("bootPeer".equalsIgnoreCase("bootPeer")){
			P2PControllerBootPeer.MakePeer();
		}else{
			P2PControllerClientPeer.MakePeer("abc@def.com");
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			if(shell.isDisposed()){
				System.out.println("Shutting down Client");
				P2PControllerClientPeer.KillPeer();
			}
		}
		
	}
}
