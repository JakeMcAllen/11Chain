package chain.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import chain.component.Transaction;

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
	private List<Guaranteer.NodeInfo> nodeList;

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
	private final int blockMaxSize = 500;
	private final int timeToSendBlock = 5;

	private boolean listenerIsActive = true;







	public Guaranteer(int socketPort) 
	{
		// Initialization of lists
		this.pool = new ArrayList<Transaction> ();

		creteNode = new Guaranteer.createNode( this );


		nodeList = new ArrayList<Guaranteer.NodeInfo> ();



		// Create thread timer
		Timer timer = new Timer();
		TimerTask task = new Guaranteer.TimerHelper(this);

		timer.schedule(task, 0, timeToSendBlock * 100);


		(new Guaranteer.getDataThread(this, socketPort)).start();


		// TODO: BlockZero sent hash and default data
		Block blockZero = new Block();
		blockZero.setIndex( "0a" );
		this.currentBlockIndex = "0a";

		this.Head = this.Tail = blockZero;



		this.socketPort = socketPort;


		// load MileStone


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
		// creation of a new block
		if ( pool.size() > blockMaxSize ) 
		{
			creteNode.newNode(pool, currentBlockIndex);
			currentTransactionNumeber = 0;
			pool.clear();
		}

		// Add transaction to pool
		if (t != null) {
			pool.add( t );
			currentTransactionNumeber++;

			// return an index to caller
			currentBlockIndex = ( Integer.parseInt( currentBlockIndex.substring(0, 1) ) + 1 ) + currentBlockIndex.substring(1); 
			return currentTransactionNumeber + "x" + currentBlockIndex;
		}

		return "";
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

		public void newNode( List<Transaction> pool, String currentBlockIndex ) 
		{
			if ( pool.size() > 0 ) {

				// TODO: Creation of a new block
				Block b = new Block();				

				currentBlockIndex = b.getIndex();


				pool.stream().forEach( e -> {

					b.setData( b.getData().toString().concat( b.getData().toString() ).getBytes() );

				});

				// TODO:
				// generate Hash and all other variables ! 
				// generate "listSCDataConnected"  
				b.setIndex(currentBlockIndex);



				// Set MileStones
				MileStone.put(b.getHash(), b);



				guaranteer.addBlock(b);


				JSONObject JObj = new JSONObject();
				JObj.put("ActionToPerform", "setNewBlock");
				JObj.put("block", b);



				// Send new block to all Node
				nodeList.stream().forEach( e -> {


					try {
						Socket s = new Socket(e.getNodeHostName(), e.getNodePort());

						OutputStream outputStream = s.getOutputStream();
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

						ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());


						objectOutputStream.writeObject(JObj);

						// transactionIndex = inStream.readUTF();
						// TODO: diversa azione in base alla risposta. 

						s.close();
						objectOutputStream.close();
						inStream.close();

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
		Tail.setNextBlock( block );	
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

			System.out.println("ResponseThread start ! ! !");
		}

		@Override
		public void run() 
		{
						
			try {

				// Response JSONObject and input obj
				JSONObject returnObj = new JSONObject();
				JSONObject jObj = null;
				
				// READ
				try (
						PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
						BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
				) {

					jObj = new JSONObject(in.readLine());

					
					// TODO: aDD NODE IN the node LIST   ! ! ! 

					
					
					System.out.println("\n\t-Serving action: " + jObj.getString("ActionToPerform")  + "\n\n");



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
				
			} catch (IOException e) {
				System.err.println("IOException in 'ResponseThread': " + e.getMessage() );		
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
						inf -> inf.getNodeIndex().equals( jo.get("Index") )
						)
				.count() > 0 ) 
		{
			return "nodeAddFail";
		}

		nodeList.add( 
				new NodeInfo(
						jo.getString("Index"),
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











	/**
	 * 
	 * 	Object that contain all info about a node of chain
	 * 
	 * 
	 * @author giorg
	 *
	 */
	private class NodeInfo {

		private String nodeIndex;
		private String nodeHostName;
		private int nodePort;

		private long confidence;

		// TODO: add other info about a single node



		public NodeInfo() {
		}

		public NodeInfo(String nodeIndex, String nodeHostName, int nodePort) {
			super();
			this.nodeIndex = nodeIndex;
			this.nodeHostName = nodeHostName;
			this.nodePort = nodePort;
		}









		public String getNodeIndex() {
			return nodeIndex;
		}

		public void setNodeIndex(String nodeIndex) {
			this.nodeIndex = nodeIndex;
		}

		public String getNodeHostName() {
			return nodeHostName;
		}


		public void setNodeHostName(String nodeHostName) {
			this.nodeHostName = nodeHostName;
		}


		public int getNodePort() {
			return nodePort;
		}


		public void setNodePort(int nodePort) {
			this.nodePort = nodePort;
		}


		public long getConfidence() {
			return confidence;
		}


		public void setConfidence(long confidence) {
			this.confidence = confidence;
		}



	}





	/*
	 * 
	 * 		MAIN
	 * 
	 * 	
	 * 	Comands: 
	 * 		1) STOP:	Ferma l'esecuzione del nodo;
	 * 		2) 	
	 * 
	 */
	public static void main(String[] args) {

		boolean isRunning = true;


		int portLocal = 8082;



		try {

			System.out.println("Start Guaranteer: ");
			Guaranteer guaranteer = new Guaranteer( portLocal );
			System.out.println("\nGuaranteer is start with success on port: " + portLocal);

			Scanner s = new Scanner(System.in);



			while ( isRunning ) {

				System.out.print("Enter a comand: ");
				String userInput = s.next();


				// TODO: Add new actions to perform
				switch (userInput) {
				case "stop": {

					isRunning = false;
					guaranteer.setListenerIsActive( false );


					System.out.println("Application stopped successfully");
					break;
				}
				case "tctg": {

					// Test connection to guaranteer
					break;
				}


				default:
					throw new IllegalArgumentException("Unexpected value: " + userInput);
				}		


			}


			s.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
