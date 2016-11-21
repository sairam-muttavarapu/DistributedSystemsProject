package com.p2p.dht;

import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;

public class P2PControllerBootPeer {

    private static Peer peer = null;
    public static final int CHUNK_SIZE = 1024;

    public P2PControllerBootPeer()  {
        
    	System.out.println("Inside P2PControllerBootPeer Constructor");
    }

    public static void MakePeer() throws NumberFormatException, Exception {

		System.out.println("======================== Starting BootPeer ========================");
		
		Number160 br = Number160.createHash("BootPeer");
		//Number160 br = new Number160(bootPeerId);
    	peer = new PeerMaker(br).setPorts(4001).makeAndListen();
    	
    	System.out.println("BootstrapPeer:"+peer.getPeerID().toString());
    	//System.out.println("BootstrapPeer Hash:"+br);
    	        	
		String curDir = System.getProperty("user.dir");
		System.out.println("Current Directory:"+curDir);
    	
    	//String content = new Scanner(new File("testFile.txt")). useDelimiter("\\Z").next();
    	//InputStream input = new FileInputStream(new File("testFile.txt"));
    	//OutputStream output;
    	
     	while(true){
    		System.out.println("bootPeer knows: "+peer.getPeerBean().getPeerMap().getAll());
    		//peer.getDistributedHashMap();
    		/*System.out.println("bootPeer knows numOfNodes: "+peer.getPeerBean().getStatistics().getEstimatedNumberOfNodes());
    		System.out.println("bootPeer knows getMaxCreating: "+peer.getConfiguration().getMaxCreating());
    		System.out.println("bootPeer knows getMaxOpenConnection: "+peer.getConfiguration().getMaxOpenConnection());
    		System.out.println("bootPeer knows isLimitTracker: "+peer.getConfiguration().isLimitTracker());
    		System.out.println("bootPeer knows isLimitTracker: "+peer.getConfiguration().getDiscoverTimeoutSec());*/
    		Thread.sleep(2000);
    	}
    	//peer.shutdown();  	
    }
    
}



