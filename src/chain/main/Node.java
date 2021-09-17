package chain.main;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

import chain.component.Transaction;



/*
 * 		NODE
 * 
 * 	Node of network
 * 
 * 		TOTO:
 * 			1) Connesione con gli altri nodi e la loro gestione ( private key, hostName and Port )
 * 			2) Guaranteer / user  connection
 * 			3) Smart contract
 * 			4) Data reading and execution of smart contract ( API for smartContract )
 * 
 */
public class Node {

	/*
	 * 
	 * 		GLOBAL VARIABLES OF NODE
	 * 	
	 * 
	 * 	Global variable list:
	 * 		1) Chain;
	 * 	 	2) Pool;
	 * 		3) Private and public key of block;
	 * 		4) Socket HostName port of current Node;
	 * 		5) Socket HostName port of guaranteer;
	 * 
	 */
	// lists
	
	Block Head;
	Block Tail;
	private HashMap<String, Block> MileStone;
	
		
	private List<Transaction> pool;

	// Keys
	private PrivateKey privateKey = null;
	private PublicKey publicKey = null;

	// socket
	private String socketHostname;
	private int socketPort;

	private String guarantorHostname;
	private int guarantorPort;





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
		
		
		
		
		// Chain inizialization
		if ( listIsEmpty() ) {
			// If list is clear create a block zero
			Block blockZero = new Block();
			
			this.Head = blockZero;
			this.Tail = blockZero;
			
			this.MileStone.put("Head", blockZero);
			this.MileStone.put("Tail", blockZero);
			this.MileStone.put("Mid", blockZero);
			
			addBlock(blockZero);
			
		} else {
			// TODO: Call for all chain block
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
	 * 	Thread " getDataThread " is used for listening action from: Guaranteer, User or Other node
	 */
	// Connection to guaranteer. 
	// Send and Get Block 
	// Get execution of Smart contract 
	// Get transaction
	
	private boolean listIsEmpty() {
		return true;
	}	
	
	private void addBlock(Block block) {
		
	}
	
	private void loadChain() {
		
	}
	
	
	class getDataThread extends Thread {
		
		@Override
		public void run() {
			
		}
		
	}
	

	


	/*
	 * 
	 * 		SMART CONTRACT
	 * 
	 */
	// Smart contract Execution

	// Smart contract control
	
	








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
	public void addTTPool(Transaction t) 
	{

		if ( t.getLenght() + pool.size() > blockMaxSize ) 
		{
			sendToGuarantor();
		}

		pool.add( t );
	}

	// Controller for sending procedure of new block
	private void sendToGuarantor() 
	{

		if ( pool.size() > 0 ) {
			// TODO: generate node
			
			// TODO: Encryption of new node 
			
			// TODO: send node to guaranteer


			pool.clear();
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

}
