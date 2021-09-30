package chain.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;
import org.json.JSONTokener;

import chain.main.Block;




/*
 * 		NODE
 * 
 * 	Node of network
 * 
 * 		TODO:
 * 			1) Connection to other Node ( private key, hostName and Port )
 * 			2) Guaranteer / user  connection
 * 			3) Smart contract
 * 			4) Data reading and execution of smart contract ( API for smartContract )
 * 			5) Control Node status from command line
 * 			6) Controller for waiting transaction. Can delete old transaction;
 * 			7) String stamp of Node action. Runtime.
 * 			8) Control of errors. Like when guaranteer is close.
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
	private HashMap<String, Block> MileStone;

	// first and last block of chain
	private Block Head;
	private Block Tail;
	

	// Keys
	private PrivateKey privateKey = null;
	private PublicKey publicKey = null;
	

	// socket port of node and hostName and Port of Guaranteer
	private int socketPort;

	private String guarantorHostName;
	private int guarantorPort;
	
	
	
	// boolean variables
	private boolean listenerIsActive = true;

	// index of node
	private String index;
	






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
	public Node( int localPort, String guarenteerHostName, int guaranteerPort )  
	{
		// call to standard constructor
		// Set Keys
		KeyPair keypair = null;
		try {
			keypair = generateRSAKkeyPair();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.privateKey = keypair.getPrivate();
		this.publicKey = keypair.getPublic();



		// Set guaranteer host and ports and socket hostName and port
		this.guarantorHostName = guarenteerHostName;
		this.guarantorPort = guaranteerPort;
		
		this.socketPort = localPort;
		
		
		// Start listener module
		(new Node.getDataThread( this.socketPort )).start();
		
		// Load chain from guaranteer
		loadChain();
			
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
	 * 		When user send a transaction is return a string that contain a link to that transaction in the block.
	 * 		This is an example: 	0x31b98d14007bdee637298086988a0bbd31184523
	 * 		In the string the number before letter "x" is the number of transaction in the block. 
	 * 		The part of string before letter "x" is the block hash.
	 * 
	 */
	
	// Call all block of chain from guaranteer
	private void loadChain() {
		
		try {
			
			// Call for all chain
			JSONObject JObj = new JSONObject();
			JObj.put("ActionToPerform", "LoadChain");

			Socket s = new Socket(this.guarantorHostName, this.guarantorPort);

			
		    // READ --- errore
		    try (
					// Socket  
		    		
		    		PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		    		BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream()) );
		    ) {

		    	out.println( JObj.toString() );
		    	
		    	Head = AddNextBlock( in );
		    	
		    }
		    

		    s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	// Metodo ricorsivo per caricare la chain
	// TODO: Da rivedere che funzioni bene ! ! ! 
	private Block AddNextBlock( BufferedReader in ) {
		
		
		try {
			String str = in.readLine();
						
			Block nBlock = Block.generateBlockFromJSON( new JSONObject( str ) );
			

			for ( int i=0; i < nBlock.SiblingBlockNumber(); i++ ) 
			{	
				Block b = AddNextBlock( in );
				nBlock.addSiblingBlock( b );
			}


			if ( nBlock.hasNextBlock() ) 
			{
				nBlock.setNextBlock( AddNextBlock( in) ); 
			}

			// TODO: TO CHECK 
			// MileStone.put(nBlock.getHash(), nBlock);
			
			return nBlock;
			
		} catch (IOException e) {
			return null;
		}
				
	}
	
	
	// Listen connection to node and start "ResponseThread" thread for serve actions
	private class getDataThread extends Thread {
		
		private int localListeningPort;
		
		
		public getDataThread( int localListeningPort) 
		{
			this.localListeningPort = localListeningPort;
		}

		@SuppressWarnings("resource")
		@Override
		public void run() {

			Object sincronizer = new Object();
			
			
			while ( listenerIsActive ) {
				
				try (
					ServerSocket serverSocket = new ServerSocket( localListeningPort );
				) {
				
					// Execute an controller for connection
					(new Node.ResponseThread(serverSocket.accept(), sincronizer)).start();
					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	private class ResponseThread extends Thread {
		
		private Socket socket = null;
		private Object sincronizer = null;
			
		public ResponseThread(Socket socket, Object sincronizer) 
		{
			this.socket = socket;
			this.sincronizer = sincronizer;
		}
		
		
		@Override
		public void run() 
		{
			
			try (
					// In out stream
		            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    		BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
		) {
				
				// Response string
				String returnString = "";
				
				String input = in.readLine();
				System.out.println("input: " + input);
				
				
				JSONObject jObj = new JSONObject( input );
				
				System.out.println("Action to perform: " + jObj.getString("ActionToPerform") );
				
				
				
				
				// TODO: Add other action to perform
				switch ( jObj.getString("ActionToPerform") ) {
				
				case "postTransactionInPool":
										
					postTransaction( jObj, this );
					break;
				
				case "readTransaction":
					
					String transactionNumber = jObj.getString("BlockHash").split("x")[0];
					returnString = getTransactionFromObject( readBlock( jObj ), transactionNumber );
					break;
					
				case "readBlock":
					
					returnString = readBlock( jObj );
					break;
				
				case "setNewBlock":
					
					synchronized (sincronizer) 
					{
						setNewBlock( jObj );
					}
					break;
				
				case "setNewSCBlock":
					
					synchronized (sincronizer) 
					{
						setNewSCBlock( jObj );
					}
					break;
				
				case "":
					
					synchronized (sincronizer) 
					{
						returnString = "";
					}
					break;
				
				default:
					throw new IllegalArgumentException("Unexpected value: " + jObj.getString("ActionToPerform"));
				}
				
				
								
				// return response 
				out.write(returnString);
			
			
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
	
	private String postTransaction( JSONObject jObj, ResponseThread thisOBJ ) {
		
		String returnString = "";
		Transaction t = Transaction.ObjFromJSON( jObj.getJSONObject( "Transaction" ) );
		
		// Get response from variables
		returnString = addTTPool(t);
		return returnString;
	}
	
	private String readBlock( JSONObject jObj ) {
		
		// TODO: case smartContract block ! ! !
		
		String returnString = "";
		String blockName = jObj.getString("BlockHash").split("x")[1];
				
		MileStone.get(blockName);
		
		return returnString;
	}
		
	private String getTransactionFromObject( String returnString, String transactionNumber ) {
		
		if ( !returnString.equals("") ) {
			
			JSONObject rObj = new JSONObject(returnString); 
			JSONObject tr = (JSONObject) rObj.get("Transactions");
			
			return tr.get( transactionNumber ).toString();
			
		} 
		
		return "";
	}
	
	private void setNewBlock( JSONObject jObj ) { 
		
		Block nBlock = Block.generateBlockFromJSON( (JSONObject) jObj.get("block") );
		
		Tail.setNextBlock( nBlock );
		Tail = nBlock;
		
	}

	private void setNewSCBlock( JSONObject jObj ) { 
		
		Block nBlock = Block.generateBlockFromJSON( (JSONObject) jObj.get("block") );

		Block parentBlock = MileStone.get( nBlock.getParentBlockHash() );
		
		parentBlock.setNextBlock( parentBlock );
		
	}
	
	/**
	 **
	 ** 	END CHAIN ACTIONS
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
	 * 		"addTTPool"			Method for send to guaranteer a new transaction
	 * 
	 * 
	 */
	private String addTTPool(Transaction t) 
	{
		// BlockNumberCounter + "x" + Hash();
		String transactionIndex = "";
	
		try (
				Socket s = new Socket(guarantorHostName, guarantorPort);
				OutputStreamWriter writer = new OutputStreamWriter(s.getOutputStream());
		) {
			
			JSONObject JObj = new JSONObject();
			JObj.put("ActionToPerform", "AddTransactionToPool");
			JObj.put("Transaction", t.toJObj() );
			
			
			// WRITE 
		    try (OutputStreamWriter out = new OutputStreamWriter( s.getOutputStream(), StandardCharsets.UTF_8)) {
		        out.write(JObj.toString());
		    }

		    
		    // READ
		    JSONTokener responseTokens = new JSONTokener(s.getInputStream());
			JSONObject jObj = new JSONObject(responseTokens);
		    			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return transactionIndex;
	}

	

	
	


	/*
	 * 
	 * 		DATA MANIPOLATION
	 * 
	 */
	// TODO: Validazione delle transazioni

	// TODO: Creazione di un blocco e validazione

	// TODO: ReadChain
	
	public void setListenerIsActive( boolean active ) {
		this.listenerIsActive = active;
	}
	
	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 
	 * 		USER / APPLICATION CONNECTION
	 * 
	 */
	// TODO: Lettura di una transazione da parte di un un utente

	// TODO: Gestisce gli utenti ? 

	public Block getHead() {
		return Head;
	}

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
		
		System.out.println("Run:");
		
		
		int portLocal = 8081;
		
		String hostGuaranteer = "localhost";
		int portGuaranteer = 8082;
		
		
		Node node = null;
		try {
			
			System.out.println("Start new Node: ");
			node = new Node( portLocal, hostGuaranteer, portGuaranteer );
			System.out.println("Node is start with success.");			
			
			
			Scanner s = new Scanner(System.in);
			
			
			while ( isRunning ) {
				
			    String userInput = s.next();
			    
			    
			    // TODO: Add new actions to perform
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
