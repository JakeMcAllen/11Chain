package chain.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

import chain.component.Block;
import chain.component.info.UsersInfo;




/*
 * 		NODE
 * 
 * 	Node of network
 * 
 * 		TODO:
 * 			2) Guaranteer / user  connection
 * 			3) Smart contract
 * 			4) Data reading and execution of smart contract ( API for smartContract )
 * 			5) Control Node status from command line
 * 			8) Control of errors. Like when guaranteer is close.
 * 			9) Data encription and decription
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
	private List<UsersInfo> userDataList;

	// first and last block of chain
	private Block Head;
	private Block Tail;


	// Keys
	private PrivateKey privateKey = null;
	private PublicKey publicKey = null;


	// socket port of node and hostName and Port of Guaranteer
	private String socketHostName;
	private int socketPort;

	private String guarantorHostName;
	private int guarantorPort;



	// boolean variables
	private boolean listenerIsActive = true;

	// index of node
	private int index;







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
	public Node(String socketHostName, int localPort, String guarenteerHostName, int guaranteerPort, int nodeIndex )  
	{
		// call to standard constructor
		// Set Keys
		KeyPair keypair = null;
		try { keypair = generateRSAKkeyPair(); } 
		catch (Exception e) { e.printStackTrace(); }

		this.privateKey = keypair.getPrivate();
		this.publicKey = keypair.getPublic();


		// list of all users of the chain to this
		userDataList = new ArrayList<UsersInfo> ();


		// Set guaranteer host and ports and socket hostName and port
		this.guarantorHostName = guarenteerHostName;
		this.guarantorPort = guaranteerPort;


		this.socketHostName = socketHostName;
		this.socketPort = localPort;

		this.index = nodeIndex;


		// Start listener module
		(new Node.getDataThread( this.socketPort, this )).start();

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

				Head = Tail = AddNextBlock( in );

			}

			s.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Metodo ricorsivo per caricare la chain
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

			return nBlock;

		} catch (IOException e) {
			return null;
		}

	}


	// Listen connection to node and start "ResponseThread" thread for serve actions
	private class getDataThread extends Thread {

		private int localListeningPort;
		private Node node;

		public getDataThread( int localListeningPort, Node node) 
		{
			this.localListeningPort = localListeningPort;
			this.node = node;
		}

		@Override
		public void run() {

			Object sincronizer = new Object();


			while ( listenerIsActive ) {

				try (
						ServerSocket serverSocket = new ServerSocket( localListeningPort );
						) {

					// Execute an controller for connection
					(new Node.ResponseThread(serverSocket.accept(), sincronizer, this.node)).start();


				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	private class ResponseThread extends Thread {

		private Socket socket = null;
		@SuppressWarnings("unused")
		private Object sincronizer = null;
		private Node node;

		public ResponseThread(Socket socket, Object sincronizer, Node node) 
		{
			this.socket = socket;
			this.sincronizer = sincronizer;
			this.node = node;
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
				JSONObject returnString = new JSONObject();
				JSONObject jObj = new JSONObject( in.readLine() );



				if ( controllUser( jObj ) ) {
					returnString.clear();


					switch ( jObj.getString("ActionToPerform") ) {

					// For Users
					case "postTransactionInPool":

						returnString = postTransaction( jObj );
						break;

					case "readTransaction":

						returnString = getTransactionFromObject( jObj );
						break;


						// For Guaranteer
					case "setNewBlock":

						setNewBlock( jObj );
						break;

					case "setNewBlockToMilestone":

						Block b = Block.generateBlockFromJSON( jObj.getJSONObject("NewBlock") );
						this.node.MileStone.put(b.getIndex(), b );
						break;


					default:
						returnString.put("Error", "Node is not able to perform action: " + jObj.getString("ActionToPerform"));
					}

				}


				out.println(returnString);

			} catch (Exception e) {
				System.err.println("Error in 'ResponseThread Node': " + e.getMessage() );
				e.printStackTrace();
			}
		}

	}








	/**
	 **
	 ** 	START CHAIN ACTIONS
	 ** 
	 ** 
	 */

	/**
	 * 	
	 * 	Send a transaction to pool of guaranteer
	 * 	Node wait a certain of time or over size certain size to send new Block.
	 * 
	 * 	Create and send to guaranteer the new block and clear pool
	 * 	If pool is not empty send data to guaranteer.
	 * 	At the and clear pool.
	 * 
	 * @param jObj input transaction
	 * @return index of transaction and block index
	 */
	private JSONObject postTransaction( JSONObject input ) {

		JSONObject nodeInfo = new JSONObject();
		nodeInfo.put("HostName", this.socketHostName);
		nodeInfo.put("Port", this.socketPort);
		nodeInfo.put("nodeIndex", this.index);


		input.put("NodeInfo", nodeInfo);

		return sendToGuaranteer( input );
	}

	/**
	 * 	Permit to check that users is able to perform actions.
	 * 	If users is new, this method, permit to register it 
	 * 
	 * 	TODO: 
	 * 		1) Fare dei controlli seri
	 * 
	 * 
	 * @param jObj input transaction
	 * @return -1 if users is not able to perform action, 1 otherwise 
	 */
	public boolean controllUser(JSONObject jo) {

		try { if ( jo.getString("user").equals("guaranteer") ) return true; } 
		catch (Exception e) {}


		try { 

			JSONObject users = jo.getJSONObject("Transaction").getJSONObject("user");

			if ( 
					this.userDataList
					.stream()
					.filter(e -> e.getIndex().equals( users.getString("index") ) )
					.count() == 0
					) {
				// If users is not present in node list sent it to guaranteer for to be checked
				JSONObject input = new JSONObject();
				input.put("ActionToPerform", "checkUser");
				input.put("user", users);

				JSONObject response = sendToGuaranteer( input );

				// Then save it on list			
				if ( response.getInt("UsersStatus") == 1 ) 
				{
					this.userDataList.add( UsersInfo.generateFromJSON( users ) );	
				} 
				else if ( response.getInt("UsersStatus") == -1 ) { return false; }

				return true;

			} else {
				// If user is present in the list check it keyString
				// TODO: modify it's private data on list

				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			
			System.out.println("Jo: " + jo);
			
			return false;
		}

	}






	private JSONObject getTransactionFromObject( JSONObject jsonObject ) {

		int transactionNumber = Integer.parseInt( jsonObject.getString("BlockIndex").split("x")[0] );
		String blockIndex = jsonObject.getString("BlockIndex").split("x")[1];

		Block nb = null;
		do {
			nb = (nb == null) ? Head : nb.getNextBlock();			
		} while ( nb.hasNextBlock() );





		Block nextB = null;
		do {

			nextB = (nextB == null) ? Head : nextB.getNextBlock();
			JSONObject jObj = nextB.toJSON();


			// cerca blocco
			if ( jObj.getString("index").equals(blockIndex) )
			{
				JSONObject datas = new JSONObject( Block.taketransactionFromData(jObj, transactionNumber) );				
				return datas;
			}

		} while ( nextB.hasNextBlock() );


		JSONObject errJSON = new JSONObject();
		errJSON.put("Error", "Transaction not fund");

		return errJSON;

	}

	private void setNewBlock( JSONObject jObj ) 
	{
		Block nBlock = Block.generateBlockFromJSON( jObj.getJSONObject("NewBlock") );


		try {
			if ( Head == null) Head = Tail = nBlock;
			else {
				Tail.setNextBlock( nBlock );
				Tail.setHasNext( true );
				Tail = nBlock;

			}

		} catch (Exception e) { e.printStackTrace(); }

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
	 * 		DATA MANIPOLATION
	 * 
	 */
	// TODO: Validazione delle transazioni

	// TODO: Creazione di un blocco e validazione

	// TODO: ReadChain

	public void setListenerIsActive( boolean active ) {
		this.listenerIsActive = active;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
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

	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	public String getSocketHostName() {
		return socketHostName;
	}

	public void setSocketHostName(String socketHostName) {
		this.socketHostName = socketHostName;
	}

	public int getSocketPort() {
		return socketPort;
	}

	public void setSocketPort(int socketPort) {
		this.socketPort = socketPort;
	}








	private JSONObject sendToGuaranteer( JSONObject input ) {

		JSONObject response = new JSONObject();

		try (
				Socket s = new Socket(this.guarantorHostName, this.guarantorPort);
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream()) );
				) {

			// READ
			out.println( input.toString() );

			response = new JSONObject( in.readLine() );


		} catch (Exception e) {
			response.put("Error to send action to guaranteer", e.getCause() );
		}

		return response;
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