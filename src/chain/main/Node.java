package chain.main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

import chain.component.Transaction;



/*
 * 		NODE
 * 
 * 	Node of network
 * 
 * 		TOTO:
 * 			1) Connection to other Node ( private key, hostName and Port )
 * 			2) Guaranteer / user  connection
 * 			3) Smart contract
 * 			4) Data reading and execution of smart contract ( API for smartContract )
 * 			5) Control Node status from command line
 * 			6) Controller for waiting transaction. Can delete old transaction;
 * 			7) String stamp of Node action. Runtime.
 * 
 * 
 * 		MODULE LIST: 
 * 			1) 	GLOBAL VARIABLES OF NODE;
 * 			2) 	GENERAL SETTING OF NODE
 * 			3) 	CONSTRUCTOR
 * 			4) 	GUARANTEER
 * 			5) 	SMART CONTRACT
 * 			6) 	POOL
 * 			7) 	DATA MANIPOLATION
 * 			8) 	USER / APPLICATION CONNECTION
 * 			9) 	ENCRIPTION
 * 			10) MAIN
 * 
 * 
 */
public class Node {

	/*
	 * 
	 * 		GLOBAL VARIABLES OF NODE
	 * 	
	 * 
	 * 	Global variable list:
	 * 		1) MileStone. It is a light method for accessing to chain
	 * 		1) Head. It's a link to first Node of chain;
	 * 	 	2) Pool;
	 * 		2) "ResponseThreadList" contain all the "ResponseThread";
	 * 		3) "currentTransactionNumeber" contain the number position of transaction in the block. Essential for get a certain transaction.
	 * 		3) Get info about the last Node insert by this node;
	 * 		3) Private and public key of block;
	 * 		4) Socket HostName port of current Node;
	 * 		5) Socket HostName port of guaranteer;
	 * 		6) Variable that describe if listener is stopped. It's permit to stop listener thread: "getDataThread". 
	 *  
	 */
	// lists
	// TODO: controllare che vada bene ... 
	private HashMap<String, Block> MileStone;

	private Block Head;
	
	private List<Transaction> pool;
	
	private List<ResponseThread> ResponseThreadList;
	
	private int currentTransactionNumeber = 0;
	
	
	// lastInsertBlock
	private Block lastInsertBlock;
	

	// Keys
	private PrivateKey privateKey = null;
	private PublicKey publicKey = null;

	// socket
	private String socketHostname;
	private int socketPort;

	private String guarantorHostname;
	private int guarantorPort;
	
	
	// boolean variables
	private boolean listenerIsActive = true;





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




	/*
	 * 
	 *  	CONSTRUCTOR
	 *  
	 *  
	 *  Constructor list:
	 *  	1) Standard None constructor. Light version that set to default;
	 *  	2) Allows you to initialize the hostName and port of this node.
	 *  
	 *  
	 *  Initialization list: 
	 *  	1) Chain;
	 *  	2) Pool list;
	 *  	3) Private and Public key;
	 *  	4) Set guaranteer hostName and Port;
	 *  	5) Set default hostName and Port for current Node. In the second constructor it is set to a arbitrary value;
	 *  	6) Start "getDataThread" thread. This module listen action from users or guaranteer.
	 *  
	 *  
	 *  CHAIN INIZIALIZATION:
	 *  	In the standard constructor is create and set Block number '0'. That is necessary for Network functionality.
	 *  	If the network already existed block zero is not create but is call the list from guaranteer.
	 *  
	 *  
	 *  Execution list of thread: 
	 *  	A timer for send every certain of time a new block to guaranteer Node.
	 *  
	 *  
	 *  CHAIN INIZIALIZATION:
	 *  	Try to Fill the chain. 
	 *  	if the guaranteer say that it's empty create a zero node and send to it. 
	 *  	If chain is not empty, current node call for all block and set them in the chain.
	 *   
	 *  
	 */
	public Node(String gH, int gP) throws Exception 
	{

		// inizialization of lists
		this.pool = new ArrayList<Transaction> ();

		

		// Create thread timer
		Timer timer = new Timer();
		TimerTask task = new Node.TimerHelper(this);

		timer.schedule(task, 0, timeToSendBlock * 100);



		// Set Keys
		KeyPair keypair = generateRSAKkeyPair();

		this.privateKey = keypair.getPrivate();
		this.publicKey = keypair.getPublic();



		// Set ports
		this.guarantorHostname = gH;
		this.guarantorPort = gP;

		this.socketHostname = "localhost";
		this.socketPort = 8080;
		
		
		// Start listener module
		new getDataThread().start();
		
		
		
		// Chain initialization
		if ( listIsEmpty() ) {
			// If list is clear create a block zero
			Block blockZero = genereteNewBlock();
			
			Head = blockZero;
			
			this.MileStone.put(blockZero.getHash(), blockZero);
			
			addBlock(blockZero);
			
		} else {
			// If chain is up to load
			loadChain();
			
		}
		
	}

	public Node( String localHostName, int localPort, String gH, int gP ) 
			throws Exception 
	{
		// call to standard constructor
		this(localHostName, localPort);

		// Socket hostName and port
		this.socketHostname = localHostName;
		this.socketPort = localPort;

	}






	/*
	 * 
	 * 		GUARANTEER 
	 * 
	 *
	 *	Method list:
	 *		"listIsEmpty" 	For call to guaranteer if chain is empty.
	 * 		"SendBlock" 		For send new block to guaranteer.
	 * 		"loadChain"		For load all block from guaranteer to local chain.
	 * 	  
	 * 	
	 * 	THREAD LIST:
	 * 		1) Thread " getDataThread " is used for listening action from: Guaranteer, User or Other node
	 * 		2) Thread " ResponseThread " is used for maintain and execute action and wait for the end of action. At the end Node send response to client
	 * 
	 * 
	 * 	RETURN STRING TO USER WHEN IS SEND A TRANSACTION:
	 * 		When user send a transaction is return a string thath contain a link to thath transaction in the block.
	 * 		This is an example: 	0x31b98d14007bdee637298086988a0bbd31184523
	 * 		In the string the number before letter "x" is the number of transaction in the block. 
	 * 		The part of string before letter "x" is the block hash.
	 * 
	 */
	// Connection to guaranteer. 
	// Send and Get Block 
	// Get execution of Smart contract 
	// Get transaction
	
	private boolean listIsEmpty() {
		
		boolean isEmpty = false;
		
		JSONObject JObj = new JSONObject();
		JObj.put("ActionToPerform", "ChainIsEmpty");
		
		try {
			
			Socket s = new Socket(guarantorHostname, guarantorPort);
			
			OutputStream outputStream = s.getOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			
			ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

			
			objectOutputStream.writeObject(JObj);
			isEmpty = inStream.readBoolean();
			
			
			s.close();
			objectOutputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return isEmpty;
	}	
	
	// Send to guaranteer
	private void addBlock(Block block) {
		
		try {
			Socket s = new Socket(guarantorHostname, guarantorPort);
			
			OutputStream outputStream = s.getOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			
			objectOutputStream.writeObject(block);
			
			
			s.close();
			objectOutputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		
	}
	
	// TODO: call all block of chain from guaranteer
	private void loadChain() {
		
		/*
		 * 
		boolean isEmpty = false;
		
		JSONObject JObj = new JSONObject();
		JObj.put("ActionToPerform", "ChainIsEmpty");
		
		try {
			
			Socket s = new Socket(guarantorHostname, guarantorPort);
			
			OutputStream outputStream = s.getOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			
			ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

			
			objectOutputStream.writeObject(JObj);
			isEmpty = inStream.readBoolean();
			
			
			s.close();
			objectOutputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		 * 
		 */
		
		
		for (;;) {
			
			addBlock( new Block() );
			
		}
		
	}
	
	// Listen connection to node and start "ResponseThread" thread for serve actions
	class getDataThread extends Thread {

		@SuppressWarnings("resource")
		@Override
		public void run() {

			Object sincronizer = new Object();
			
			while ( listenerIsActive ) {
				
				try {
					ServerSocket serverSocket = new ServerSocket( socketPort );

					// Execute an controller for connection
					new Node.ResponseThread(serverSocket.accept(), sincronizer, currentTransactionNumeber++).start();
										
				} catch (SocketException se) {
					System.exit(0);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}

	}
	
	private class ResponseThread extends Thread {
		
		private Socket socket = null;
		private Object sincronizer = null;
		private int BlockNumberCounter = 0;
				
		public ResponseThread(Socket socket, Object sincronizer, int BlockNumberCounter) 
		{
			this.socket = socket;
			this.sincronizer = sincronizer;
			this.BlockNumberCounter = BlockNumberCounter;
		}
		
		
		@Override
		public void run() {
			
			try {
				
				// Response string
				String returnString = "";
				
				
				// In out stream
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			    
				JSONObject jObj = (JSONObject) inStream.readObject();
				
				
				
				// TODO: Add other action
				switch ( jObj.getString("ActionToPerform") ) {
				
				case "postTransactionInPool": {
										
					postTransaction( jObj, this, BlockNumberCounter );
					break;
				}
				
				case "readTransaction":
					
					String transactionNumber = jObj.getString("BlockHash").split("x")[0];
					returnString = readBlock( jObj );
					getTransactionFromObject( returnString, transactionNumber );
					break;
					
					
				case "readBlock":
					
					returnString = readBlock( jObj );
					break;
				
				case "": {
					
					synchronized (sincronizer) {
						returnString = "";
					}
					
					break;
				}
				
				default:
					throw new IllegalArgumentException("Unexpected value: " + jObj.getString("ActionToPerform"));
				}
				
				
								
				// return response 
				out.write(returnString);

				// close socket and stream
				socket.close();
				inStream.close();
				out.close();
			
			
			} catch (Exception e) {
				System.err.println("Error in 'ResponseThread': " + e.getMessage() );		
			}
		}
		
	}
	
	
	
	
	
	
	/**
	 **
	 ** 	START CHAIN ACTIONS
	 ** 
	 ** 
	 */
	
	private String postTransaction( JSONObject jObj, ResponseThread thisOBJ, int BlockNumberCounter ) {
		
		String returnString = "";
		
		Transaction t = Transaction.ObjFromJSON( jObj.getJSONObject( "Transaction" ) );
		addTTPool(t);
		
		// Add to list and wait for a block creation
		ResponseThreadList.add( thisOBJ );
		try { 
			thisOBJ.wait(); 
		} catch (InterruptedException e) { e.printStackTrace(); }
		
		// Get response from variables
		returnString = BlockNumberCounter + "x" + lastInsertBlock.getHash();
		
		return returnString;
	}
	
	private String readBlock( JSONObject jObj ) {
		
		String returnString = "";
		String blockName = jObj.getString("BlockHash").split("x")[1];
				
		MileStone.get(blockName);
		
		return returnString;
	}
	
	
	
	private String getTransactionFromObject( String returnString, String transactionNumber ) {
		
		if ( !returnString.equals("") ) {
			
			JSONObject rObj = new JSONObject(returnString); 
			JSONObject tr = (JSONObject) rObj.get("Transactions");
			
			returnString =  tr.get( transactionNumber ).toString();
			
		}
		
		return returnString;
	}
	
	
	/**
	 **
	 ** 	START CHAIN ACTIONS
	 ** 
	 ** 
	 */
	
	
	
	
	
	
	

	
	
	


	/*
	 * 
	 * 		SMART CONTRACT
	 * 
	 */
	// TODO: Smart contract Execution

	// TODO: Smart contract control
	
	








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
	private void addTTPool(Transaction t) 
	{

		if ( t.getLenght() + pool.size() > blockMaxSize ) 
		{
			sendToGuarantor();
		}

		pool.add( t );
		
	}
	
	// sending procedure for send new block to guaranteer to be checked
	private void sendToGuarantor() 
	{

		if ( pool.size() > 0 ) {
			// Generate node
			Block n = genereteNewBlock();
			
			// Encryption of new node 
			n.encriptNode();
			
			
			// Send node to guaranteer and receive definitive block
			
			Block fixedBlock = null;
			
			JSONObject JObj = new JSONObject();
			JObj.put("ActionToPerform", "AddBlockToChain");
			JObj.put("Block", n);
			
			try {
				
				Socket s = new Socket(guarantorHostname, guarantorPort);
				
				OutputStream outputStream = s.getOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
				
				ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

				
				objectOutputStream.writeObject(JObj);
				fixedBlock = (Block) inStream.readObject();
				
				
				s.close();
				objectOutputStream.close();
				
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			

			
			// Set variable about block info 
			if ( fixedBlock != null ) lastInsertBlock = n;
			
			// Notify all waiting client to execute response
			for( ResponseThread rt : ResponseThreadList ) 
			{ 
				rt.notify();	
			}
			
			currentTransactionNumeber = 0;
			pool.clear();
			
			// Set MileStones
			MileStone.put(fixedBlock.getHash(), fixedBlock);
			
		}

	}

	// Timer
	private class TimerHelper extends TimerTask 
	{
		private Node node;

		public TimerHelper(Node node) 
		{
			this.node = node;
		}

		@Override
		public void run()
		{	        
			node.sendToGuarantor();
		}
	}


	

	
	


	/*
	 * 
	 * 		DATA MANIPOLATION
	 * 
	 */
	// TODO: Validazione delle transazioni

	// TODO: Creazione di un blocco e validazione

	// TODO: ReadChain
	
	// creation of a block
	private Block genereteNewBlock() {
		
		Block block = new Block();
		
		
		if (Head == null ) {
			// TODO block zero 
			
			
		} else {
			// TODO: Block from pool
			
		}
		
		
		return block;
		
	}
	
	public void setListenerIsActive( boolean active ) {
		this.listenerIsActive = active;
	}
	
	
	
	
	
	
	
	
	/*
	 * 
	 * 		USER / APPLICATION CONNECTION
	 * 
	 */
	// TODO: Lettura di una transazione da parte di un un utente

	// TODO: Gestisce gli utenti ? 

	// TODO:  get of variables
	public PublicKey getPublicKey() 
	{
		return this.publicKey;
	}



	
	
	
	
	/*
	 * 
	 *  	ENCRIPTION
	 * 
	 * 	
	 * 	Method list:
	 *  	"dataEncryption"  	Encryption function which converts the plainText into a cipherText using private Key.
	 *  	"stringEncription"	Like method "dataEncryption" but return a string.
	 *  	"dataDecryption"		De-encryption function which converts the encrypted text back to the original.
	 *  
	 */	
	private static KeyPair generateRSAKkeyPair() throws Exception 
	{
		SecureRandom secureRandom = new SecureRandom();
		KeyPairGenerator keyPairGenerator  = KeyPairGenerator.getInstance("RSA");

		keyPairGenerator.initialize( 2048, secureRandom);
		return keyPairGenerator.generateKeyPair();
	}
	
	public byte[] dataEncryption ( String plainText ) throws Exception 
	{
		Cipher cipher = Cipher.getInstance("RSA");

		cipher.init( Cipher.ENCRYPT_MODE, this.privateKey);

		return cipher.doFinal( plainText.getBytes());
	}

	public String stringEncription ( String plainText ) throws Exception 
	{
		return DatatypeConverter.printHexBinary( dataEncryption( plainText ) );
	}

	public String dataDecryption( byte[] cipherText) throws Exception 
	{
		Cipher cipher = Cipher.getInstance("RSA");

		cipher.init(Cipher.DECRYPT_MODE, this.publicKey);
		byte[] result = cipher.doFinal(cipherText);

		return new String(result);
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
		
		
		String hostLocal = "";
		int portLocal = 1;
		
		String hostGuaranteer = "";
		int portGuaranteer = 1;
		
		
		Node node = null;
		try {
			node = new Node( hostLocal, portLocal,
						hostGuaranteer, portGuaranteer );
			
			Scanner s = new Scanner(System.in);
			
			
			while ( isRunning ) {
				
			    String userInput = s.next();
			    
			    
			    // TODO: Add new actions
			    switch (userInput) {
				case "stop": {
					
					isRunning = false;
					node.setListenerIsActive( false );
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
