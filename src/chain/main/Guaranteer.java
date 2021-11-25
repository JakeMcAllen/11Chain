package chain.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import chain.component.Block;
import chain.component.Transaction;
import chain.component.info.NodeInfo;
import chain.component.info.UsersInfo;
import chain.extraClasses.GuaranteerNodePersistantManager;


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
	 *		3) variable that update to Guarenteer's thread that must stop
	 *		4) Path where save info about Guaranteer 
	 * 
	 * 
	 * 	Time for send a new block to guaranteer is set in second, but in the thread " TimerHelper " is used in milliseconds
	 *  
	 */
	// TODO: Per ora è in numero di transazioni. Vai a modificare il controllo su "pool.size"
	private final int blockMaxSize = 500;
	private final int timeToSendBlock = 20;

	private boolean listenerIsActive = true;











	public Guaranteer() throws FileNotFoundException, IOException, ParseException {
		loadGuaranteer();
		loadComponents();

	}


	public Guaranteer(int socketPort) 
	{		

		this.socketPort = socketPort;


		// Control if file is present
		loadComponents();

		// BlockZero sent hash and default data
		Block blockZero = new Block();
		blockZero.setIndex( "0a" );
		blockZero.setData("BlockZero".getBytes());
		this.currentBlockIndex = "1a";

		this.Head = this.Tail = blockZero;
		MileStone.put(blockZero.getIndex(), blockZero);

	}



	
	
	public void loadComponents() {
		
		// control if file is present
		controlFilePath();


		// Initialization of lists
		this.pool = new ArrayList<Transaction> ();
		this.MileStone = new HashMap<String, Block>();

		nodeList = new ArrayList<NodeInfo> ();
		userDataList = new ArrayList<UsersInfo> ();

		// Thread for creation of a new Block
		creteNode = new Guaranteer.createNode( this );


		// Create thread timer
		Timer timer = new Timer();
		TimerTask task = new Guaranteer.TimerHelper(this);

		timer.schedule(task, 0, timeToSendBlock * 100);


		// thread that listen node connection to localhost
		(new Guaranteer.getDataThread(this, socketPort)).start();



		// Thread that update the Milestone every day at midnight
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		( new Timer() ).schedule( new updateMilestone( this ) , calendar.getTime(), 86400000 );

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
		// TODO: control size of all transaction
		if ( ( t == null || pollSize(pool) > blockMaxSize ) && pool.size() > 0 ) 
		{
			currentTransactionNumeber = 0;
			creteNode.newBlock(pool, currentBlockIndex);
			
			currentBlockIndex = ( Integer.parseInt( currentBlockIndex.substring(0, 1) ) + 1 ) + currentBlockIndex.substring(1); 
			pool.clear();
		}

		// Add transaction to pool
		if (t != null) {
			pool.add( t );

			// return an index to caller
			transactionIndex = ++currentTransactionNumeber + "x" + currentBlockIndex;

		}

		return transactionIndex;
	}

	private int pollSize(List<Transaction> poolList) {

		int poollWeight = 0;

		if (poolList.size() != 0 && poolList != null ) 
		{
			for ( Transaction t : poolList ) 
			{			
				poollWeight += t.getLenght();
			}
		}

		return poollWeight;
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

				Block b = new Block();
				b.setIndex(currentBlockIndex);
				b.importDataFromPool( pool );
				b.setIndex(currentBlockIndex);


				// TODO: Fare in modo che la chaive creata sia presa da una lista privata
				// b.generateHash( userDataList.get( ( (Transaction) pool.get( pool.size() -1 ) ).getUserID() ) );

				guaranteer.addBlock(b);
				this.guaranteer.lastInsertBlock = b;


				// Send new block to all Node
				this.guaranteer.sendToAllNodeJSON("setNewBlock", b.toJSON());

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
			// call for a new block with a partial number of datas
			guaranteer.addTTPool( null );
			if (!guaranteer.listenerIsActive) System.exit(0);
		}
	}


	public void addBlock( Block block) 
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



				// READ
				try (
						PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
						BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
						) {

					jObj = new JSONObject(in.readLine());

					// aDD NODE IN the node LIST
					setNodeToList(jObj);




					switch ( jObj.getString("ActionToPerform") ) {

					case "postTransactionInPool":
						returnObj.put("BlockIndex", this.guaranteer.addTTPool( new Transaction( jObj.getJSONObject("Transaction") ) ) );
						break;

					case "newNode": 
						synchronized (sincronizer) {
							returnObj.put("ResponseString", addNewNode(jObj) );
						}
						break;

					case "LoadChain":
						returnObj.put("ResponseString", loadChain(out, Head) );
						break;


					case "checkUser":
						returnObj.put("UsersStatus", checkUser( jObj ) );
						break;

					default:
						returnObj.put("ResponseString", "Inaspected error. Stop and restart application." );
						throw new IllegalArgumentException("Unexpected value: " + jObj.getString("ActionToPerform"));
					}



					// WRITE return message
					returnObj.put("user", "guaranteer");
					out.println( returnObj.toString() );

				}


				// close socket and stream
				socket.close();


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
	

	public void setNodeToList(JSONObject jObj ) {

		if ( jObj.has("NodeInfo") ) {

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
	}

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
			Block b = this.guaranteer.getLastBlockInserted();

			// Set new milestone
			this.guaranteer.setNewMileStone( b );

			// Send new block at milestone of all Node
			this.guaranteer.sendToAllNodeJSON( "setNewBlockToMilestone", b.toJSON() );

		}

	}

	// TODO: Fare dei controlli seri su gli utenti
	private int checkUser(JSONObject jO) {
		JSONObject user = jO.getJSONObject("user");

		try {
			this.userDataList.add( UsersInfo.generateFromJSON( user ) );
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return 1;
	}
	
	public void updateTailOnPersistance(Block b) {
		if (this.Head == null) {
			this.Head = this.Tail = b;
		} else  {
			this.Tail = this.Tail.setNextBlock(b);	
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

		if ( nodeList.stream()
				.filter( inf -> inf.getNodeIndex() == jo.getInt("Index"))
				.count() > 0 
				) { return "nodeAddFail"; }

		nodeList.add( 
				new NodeInfo(
						jo.getInt("Index"),
						jo.getString("HostName"),
						jo.getInt("Port")
						) );


		return "nodeAddSuccess";
	}


	private void sendToAllNodeJSON( String message, JSONObject obj ) {
		nodeList.stream().forEach( e -> {

			try {

				try (
						Socket s = new Socket(e.getNodeHostName(), e.getNodePort());

						PrintWriter out = new PrintWriter(s.getOutputStream(), true);
						BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream()) );
						) {

					JSONObject jObj = new JSONObject();
					jObj.put("ActionToPerform", message);
					jObj.put("NewBlock", obj );
					jObj.put("user", "guaranteer");


					out.println( jObj.toString() );

				}


			} catch (Exception e1) {
				e1.printStackTrace();
			}

		});
	}

	public boolean getListenerIsActive() {
		return this.listenerIsActive;
	}

	public void setListenerIsActive(boolean state) throws IOException {
		if (!state) { 
			// try to create a last block and save all data in a file
			currentBlockIndex = Tail.getIndex();
			System.out.println("currentBlockIndex: " + currentBlockIndex);
			
			addTTPool( null );
			saveGuaranteer();
		}

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

	public int getSocketPort() {
		return socketPort;
	}








	





	/*
	 * 
	 * 	Files actions methods
	 * 
	 * 
	 */

	// TODO: PErmettere di aggiungere ogni file in un path diverso e permettere i backUp cambiando ogni volta il nome del file con la data odierna
	// TOOD: Fix "loadGuaranteer" che possa scegliere la versione meno vecchia per ogni file
	GuaranteerNodePersistantManager gpm;

	private String pathFiles = "D:\\Lavoro\\AllenaBusinessChain\\src\\chain\\files\\guaranteer\\";

	private String GNL_F = "GuaranteerNodeList.csv";
	private String GUI_F = "GuaranteerUsersInfo.csv";
	private String GM_F = "GuaranteerMilestone.csv";
	private String GEXT_F = "GuaranteerExtraInfo.txt";
	private String GC_F = "GuaranteerChainCopy.csv";


	private void controlFilePath() {
		if (gpm == null) gpm = new GuaranteerNodePersistantManager(pathFiles);

		// check that all necessary file are present
		gpm.checkFile(this.GNL_F);
		gpm.checkFile(this.GUI_F);
		gpm.checkFile(this.GM_F);
		gpm.checkFile(this.GEXT_F);
		gpm.checkFile(this.GC_F);
	}

	private void saveGuaranteer() throws IOException {
		if (gpm == null ) controlFilePath();		

		int cont = 0;

		Block block = Head;
		do {
			cont++;
			block = block.getNextBlock();
		} while ( block.hasNextBlock() );


		System.out.println("cont: " + cont);


		gpm.saveCSVFile(
				this.nodeList,
				NodeInfo.listGlobalVariablesForPersistance,
				this.GNL_F
				);

		gpm.saveCSVFile(
				this.userDataList,
				UsersInfo.listGlobalVariablesForPersistance,
				this.GUI_F
				);

		gpm.saveCSVFile(
				this.MileStone,
				Block.listGlobalVariablesForPersistance,
				this.GM_F
				);		

		// Tutti i blocchi del garante  ! ! ! 
		gpm.saveChain(
				Head,
				GC_F
				);




		JSONObject jo = new JSONObject();
		jo.put("socketPort", this.socketPort);

		gpm.saveTxTFile(jo, GEXT_F);
	}


	private void loadGuaranteer() throws FileNotFoundException, IOException, ParseException {
		if (gpm == null ) controlFilePath();		

		try {

			this.MileStone = gpm.CSVReaderHashMap(GM_F);
			this.nodeList = gpm.CSVReaderListNodeInfo(GNL_F);
			this.userDataList = gpm.CSVReaderListUsersInfo(GUI_F);

			this.socketPort = Integer.parseInt( gpm.JSONReadFromFile("socketPort", GEXT_F) );


			gpm.loadChain(this, GC_F, Head, Tail);

			currentBlockIndex = this.Tail.getIndex();
			
			

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	



}
