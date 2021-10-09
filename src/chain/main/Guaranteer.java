package chain.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import chain.component.Block;
import chain.component.Transaction;
import chain.component.info.NodeInfo;
import chain.component.info.UsersInfo;

/*
 * 
 * 	TODO:
 * 		Aggiungere azioni disponibili da richiamare
 * 		Milestone creato su base giornaliera ( mandare milestone )
 * 
 * 
 */
public class Guaranteer {

	/*
	 * 
	 * 		GLOBAL VARIABLES OF NODE
	 * 	
	 * 
	 * 	Global variable list:
	 * 
	 */
	private Block Head;
	private Block Tail;


	// lista delle info del nodo
	private List<NodeInfo> nodeList;
	private List<UsersInfo> userDataList;


	private List<Transaction> pool;
	private Block lastInsertBlock;

	private int currentTransactionNumeber;
	private String currentBlockIndex;


	// TODO: controllare che vada bene ... 
	private HashMap<String, Block> MileStone;


	private Guaranteer.createNode creteNode;

	private int socketPort;






	/*
	 * 
	 * 	 	GENERAL SETTING OF NODE
	 * 
	 * 
	 * 	This setting are constant used in all the node.
	 * 
	 * 	Constant list:
	 * 		1) Max bytes for a block;
	 * 		2) Time that local thread " TimerHelper " wait to try to send a Block to guaranteer;
	 * 
	 * 
	 * 	Time for send a new block to guaranteer is set in second, but in the thread " TimerHelper " is used in milliseconds
	 *  
	 */
	// TODO: Per ora � in numero di transazioni. Vai a modificare il controllo su "pool.size"
	private final int blockMaxSize = 5;
	private final int timeToSendBlock = 5;

	private boolean listenerIsActive = true;







	public Guaranteer(int socketPort) 
	{
		// Initialization of lists
		this.pool = new ArrayList<Transaction> ();
		this.socketPort = socketPort;

		creteNode = new Guaranteer.createNode( this );

		nodeList = new ArrayList<NodeInfo> ();
		userDataList = new ArrayList<UsersInfo> ();




		// Create thread timer
		Timer timer = new Timer();
		TimerTask task = new Guaranteer.TimerHelper(this);

		timer.schedule(task, 0, timeToSendBlock * 100);


		// thread that listen node connection to localhost
		(new Guaranteer.getDataThread(this, socketPort)).start();

		

		// TODO: BlockZero sent hash and default data
		Block blockZero = new Block();
		blockZero.setIndex( "0a" );
		this.currentBlockIndex = "1a";

		this.Head = this.Tail = blockZero;



		
		// Load first MileStone
		MileStone.put(blockZero.getIndex(), blockZero);

		
		// thread that update the Milestone every midnigth
		Calendar calendar = Calendar.getInstance();
		int period = 100000; //10secs
		
		
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);		
	    
	    ( new Timer() ).schedule( new updateMilestone( this ) , calendar.getTime(), period );
	    
	}






	




	/*
	 * 
	 * 		POOL
	 * 
	 * 
	 * 	Node wait a certain of time or over size certain size to send new Block.
	 * 
	 * 	Create and send to guaranteer the new block and clear pool
	 * 	If pool is not empty send data to guaranteer.
	 * 	At the and clear pool.
	 * 
	 * 
	 * 	Method list:
	 * 		"addTTPool"			Method for insert into pool a transaction. If pool is full call Method "sendToGuarantor";
	 * 		"sendToGuarantor"	Create and send a block to guaranteer;
	 * 
	 * 
	 * 	Definition of a Thread timer call "TimerHelper". 
	 * 		Every certain of time send an order to create and send a new block.
	 * 		For not send an empty block there is a control for check if pool is not empty. 
	 * 
	 */
	private String addTTPool(Transaction t) 
	{
		String transactionIndex = "";

		// creation of a new block
		if ( ( pool.size() > blockMaxSize || t == null ) && pool.size() != 0 ) 
		{
			currentTransactionNumeber = 0;
			creteNode.newBlock(pool, currentBlockIndex);
			
			currentBlockIndex = ( Integer.parseInt( currentBlockIndex.substring(0, 1) ) + 1 ) + currentBlockIndex.substring(1); 
			pool.clear();
		}

		// Add transaction to pool
		if (t != null) {
			pool.add( t );
			// currentTransactionNumeber++;


			// return an index to caller
			transactionIndex = ++currentTransactionNumeber + "x" + currentBlockIndex;

		}

		return transactionIndex;
	}


	// sending procedure for send new block to guaranteer to be checked
	private class createNode extends Thread 
	{	
		private Guaranteer guaranteer;

		public createNode( Guaranteer guaranteer ) 
		{
			this.guaranteer = guaranteer;
			System.out.println("\t-Listener 'createNode'\t\t: Is Start");
		}

		public void newBlock( List<Transaction> pool, String currentBlockIndex ) 
		{
			if ( pool.size() > 0 ) {

				// TODO: Creation of a new block
				Block b = new Block();
				b.setIndex(currentBlockIndex);
				
				b.importDataFromPool( pool );
				
				

				// TODO:
				// generate Hash and all other variables ! 
				// generate "listSCDataConnected"  
				b.setIndex(currentBlockIndex);


				// TODO: Fare in modo che la chaive creata sia presa da una lista privata
				// b.generateHash( userDataList.get( ( (Transaction) pool.get( pool.size() -1 ) ).getUserID() ) );

				guaranteer.addBlock(b);
				this.guaranteer.lastInsertBlock = b;


				// Send new block to all Node
				nodeList.stream().forEach( e -> {

					try {
						
					    try (
								Socket s = new Socket(e.getNodeHostName(), e.getNodePort());

					            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
					    		BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream()) );
					    ) {
					    	
					    	JSONObject jObj = new JSONObject();
					    	jObj.put("ActionToPerform", "setNewBlock");
					    	jObj.put("NewBlock", b.toJSON() );

					    	
					    	out.println( jObj.toString() );

					    }
						
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				});

			}

			if (!guaranteer.listenerIsActive) System.exit(0);
		}
	}

	// Timer
	private class TimerHelper extends TimerTask 
	{
		private Guaranteer guaranteer;

		public TimerHelper(Guaranteer guaranteer) 
		{
			this.guaranteer = guaranteer;
		}

		@Override
		public void run()
		{
			// TODO: create a consistent input
			guaranteer.addTTPool( null );
			if (!guaranteer.listenerIsActive) System.exit(0);
		}
	}


	private void addBlock( Block block) 
	{
		Tail = Tail.setNextBlock( block );
	}











	/*
	 * 
	 * 	LISTENER OF CONNECTIONS
	 * 
	 * 
	 * 
	 * 
	 * 	LISTENER OF CALL FROM NODES
	 * 
	 * 	TODO: 
	 * 		1) Add control of node truthfulness; 
	 * 
	 */

	// Listen connection to node and start "ResponseThread" thread for serve actions
	private class getDataThread extends Thread 
	{
		private Guaranteer guaranteer;
		private int socketPort;

		public getDataThread(Guaranteer guaranteer, int socketPort) {
			this.guaranteer = guaranteer;
			this.socketPort = socketPort;

			System.out.println("\t-Listener 'getDataThread'\t: Is Start");
		}

		@Override
		public void run() 
		{
			// TODO: Add to arguments
			Object sincronizer = new Object();

			while ( guaranteer.getListenerIsActive() ) {

				try (
					ServerSocket serverSocket = new ServerSocket( this.socketPort );
				) {

					// Execute an controller for serve action
					(new Guaranteer.ResponseThread(serverSocket.accept(), sincronizer, this.guaranteer)).start();


					// Execute an controller for connection


				} catch (SocketException se) {
					se.printStackTrace();
					System.exit(0);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			if (!guaranteer.listenerIsActive) System.exit(0);
		}
	}

	private class ResponseThread extends Thread {

		private Socket socket = null;
		private Object sincronizer = null;
		private Guaranteer guaranteer;

		public ResponseThread(Socket socket, Object sincronizer, Guaranteer guaranteer) 
		{
			this.socket = socket;
			this.sincronizer = sincronizer;
			this.guaranteer = guaranteer;
		}

		@Override
		public void run() 
		{

			try {

				// Response JSONObject and input obj
				JSONObject returnObj = new JSONObject();
				JSONObject jObj = null;

				String currentIndex = currentBlockIndex;

				
				
				// READ
				try (
						PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
						BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
						) {

					jObj = new JSONObject(in.readLine());
					
					
					// aDD NODE IN the node LIST
					if ( jObj.has("NodeInfo") ) 
					{
						JSONObject nodeInfo = (JSONObject) jObj.get("NodeInfo");
						
						boolean isPresent = false;
						for (NodeInfo nt : nodeList ) {
							
							if ( nt.getNodeIndex() == nodeInfo.getInt("nodeIndex") ) 
							{
								isPresent = true;
							}
							
						}
						
						if ( !isPresent || nodeList.isEmpty() ) {
							NodeInfo ni = new NodeInfo();
							ni.setNodeHostName( nodeInfo.getString("HostName") );
							ni.setNodePort( nodeInfo.getInt("Port") );
							ni.setNodeIndex( nodeInfo.getInt("nodeIndex") );
							ni.setConfidence( 1 );
							
							nodeList.add( ni );
						}
						
					}					
					
					

					// TODO: Add other action to perform
					switch ( jObj.getString("ActionToPerform") ) {

					case "postTransactionInPool":

						JSONObject trOb = (JSONObject) jObj.get("Transaction");
						returnObj.put("BlockIndex", this.guaranteer.addTTPool( new Transaction( trOb ) ) );
						break;

					case "setNewNodeConnection":

						synchronized (sincronizer) 
						{
							// set new NodeInfo
							returnObj.put("ResponseString",  "");
						}
						break;

					case "newNode": 
												
						synchronized (sincronizer) {
							returnObj.put("ResponseString", addNewNode(jObj) );
						}
						break;

					case "LoadChain":
						returnObj.put("ResponseString", loadChain(out, Head) );
						break;
						
					default:
						returnObj.put("ResponseString", "Inaspected error. Stop and restart application." );
						throw new IllegalArgumentException("Unexpected value: " + jObj.getString("ActionToPerform"));
					}


					// WRITE return message
					out.println( returnObj.toString() );

				}


				// close socket and stream
				socket.close();
				
				
				// TODO: If new block send it to all nodes !
				if ( currentIndex != currentBlockIndex) {
					
					
				}


			} catch (IOException e) {
				System.err.println("IOException in 'ResponseThread' Guarantter: " + e.getMessage() );		
			}
		}

	}

	

	


	

	/* 
	 * 
	 * 	START ACTION ON THE CHAIN
	 * 
	 */
	
	public String loadChain(PrintWriter out, Block currentBlock) {


		out.println( currentBlock.toJSON().toString() );

		for ( int i=0; i < currentBlock.getNumberOfSiblingBlock(); i++ ) 
		{
			loadChain(out, currentBlock.getSiblingBlock().get(i));
		}

		if (currentBlock.hasNextBlock()) 
		{
			loadChain(out, currentBlock.getNextBlock() );
		}


		return "OK";
	}

	private class updateMilestone extends TimerTask {
		private Guaranteer guaranteer;
		
		public updateMilestone(Guaranteer guaranteer) {
			this.guaranteer = guaranteer;
		}

		
		@Override
		public void run() {
			this.guaranteer.setNewMileStone( 
						this.guaranteer.getLastBlockInserted()
					);
		}
		
	}

	/* 
	 * 
	 * 	END ACTION ON THE CHAIN
	 * 
	 */












	/*
	 * 
	 * 	Action, GET AND SET
	 * 
	 */
	private String addNewNode(JSONObject jObj) {

		JSONObject jo = jObj.getJSONObject("Node");

		if ( nodeList
				.stream()
				.filter( 
						inf -> inf.getNodeIndex() == jo.getInt("Index")
				)
				.count() > 0 ) 
		{
			return "nodeAddFail";
		}

		nodeList.add( 
				new NodeInfo(
						jo.getInt("Index"),
						jo.getString("HostName"),
						jo.getInt("Port")
						) );


		return "nodeAddSuccess";
	}

	public boolean getListenerIsActive() {
		return this.listenerIsActive;
	}

	public void setListenerIsActive(boolean state) {
		this.listenerIsActive = state;
	}

	public HashMap<String, Block> getMileStone() {
		return MileStone;
	}

	public void setNewMileStone(Block b) {
		this.MileStone.put(b.getIndex(), b);
	}

	private Block getLastBlockInserted() {
		return this.lastInsertBlock;
	}
	
	
	
}
