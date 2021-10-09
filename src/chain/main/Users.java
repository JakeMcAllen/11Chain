package chain.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.json.JSONObject;

import chain.component.Transaction;


public class Users {
	
	
	private String index;	
	private String name;
	private String surname;
	
	
	// Keys
	private PrivateKey privateKey = null;
	private PublicKey publicKey = null;	
	private long balance;
	
	
	// Final datas about Users
	private byte[] data;

	
	
	
	
	
	
	// Permissions   from 0 to 5   ( from Low to Hight )
		private int permissions;
		
	
	// TODO: List about past action of user
	
	
	
	// Info about node connection
	private int nodeConnectionIndex;
	private String nodeConnectionHostName;
	private int nodeConnectionPort;
	
	
	
	
	// Company info
	private String CompanyName;
	private int CompanyIndex;
	private int CompanyNodeIndex;
	
	
	
	
	public Users() {}

	public Users(String index, long balance, int permissions) {
		super();
		this.index = index;
		this.balance = balance;
		this.permissions = permissions;
	}



	public Users(String index, PrivateKey privateKey, PublicKey publicKey, long balance, int permissions,
			byte[] datas) {
		super();
		this.index = index;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
		this.balance = balance;
		this.permissions = permissions;
		this.data = datas;
	}


	public Users(String index, String name, PrivateKey privateKey, PublicKey publicKey, long balance, int permissions,
			byte[] data, int nodeConnectionIndex, String nodeConnectionHostName, int nodeConnectionPort,
			String companyName, int companyIndex, int companyNodeIndex) {
		super();
		this.index = index;
		this.name = name;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
		this.balance = balance;
		this.permissions = permissions;
		this.data = data;
		this.nodeConnectionIndex = nodeConnectionIndex;
		this.nodeConnectionHostName = nodeConnectionHostName;
		this.nodeConnectionPort = nodeConnectionPort;
		CompanyName = companyName;
		CompanyIndex = companyIndex;
		CompanyNodeIndex = companyNodeIndex;
	}

	


















	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
	
	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	public int getPermissions() {
		return permissions;
	}

	public void setPermissions(int permissions) {
		this.permissions = permissions;
	}

	public byte[] getData() {
		return data;
	}

	public void setDatas(byte[] data) {
		this.data = data;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNodeConnectionIndex() {
		return nodeConnectionIndex;
	}

	public void setNodeConnectionIndex(int nodeConnectionIndex) {
		this.nodeConnectionIndex = nodeConnectionIndex;
	}

	public String getNodeConnectionHostName() {
		return nodeConnectionHostName;
	}

	public void setNodeConnectionHostName(String nodeConnectionHostName) {
		this.nodeConnectionHostName = nodeConnectionHostName;
	}

	public int getNodeConnectionPort() {
		return nodeConnectionPort;
	}

	public void setNodeConnectionPort(int nodeConnectionPort) {
		this.nodeConnectionPort = nodeConnectionPort;
	}

	public String getCompanyName() {
		return CompanyName;
	}

	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}

	public int getCompanyIndex() {
		return CompanyIndex;
	}

	public void setCompanyIndex(int companyIndex) {
		CompanyIndex = companyIndex;
	}

	public int getCompanyNodeIndex() {
		return CompanyNodeIndex;
	}

	public void setCompanyNodeIndex(int companyNodeIndex) {
		CompanyNodeIndex = companyNodeIndex;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	

	

	
	
	
	
	/*
	 * 
	 * 
	 * START ACTIVITY DEFINITION
	 * 
	 * 
	 */
	
	// set Node info
	public void setCurrentNode(Node n) {
		setNodeConnectionIndex( n.getIndex() );
		setNodeConnectionHostName( n.getSocketHostName() );
		setNodeConnectionPort( n.getSocketPort() );
		
	}
	
	// Private and Public Key
	public void generateRSAKkeyPair() 
	{
		SecureRandom secureRandom = new SecureRandom();
		KeyPairGenerator keyPairGenerator = null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		keyPairGenerator.initialize( 2048, secureRandom);
		KeyPair key = keyPairGenerator.generateKeyPair();
		
		
		this.publicKey = key.getPublic();
		this.privateKey = key.getPrivate();
		
		
		System.out.println("\n\n----------KEYS CREATION END----------" + "\n" + 
				"Private key: " + key.getPrivate() + "\n" + 
				"Public key: " + key.getPublic() + "\n" + 
				"----------KEYS CREATION START----------\n\n" );
	}
	
	// Send transaction
	// TODO: When error try to resends it
	public JSONObject sendTransaction( Transaction t, String nodeHostname, int nodePort, int nodeIndex ) 
	{
		
		String returnStr = "";
		t.setNodeDeestination(nodeHostname, nodePort, nodeIndex);
		
		JSONObject jObj = new JSONObject();
		jObj.put("ActionToPerform", "postTransactionInPool");
		jObj.put("Transaction", t.toJObj());
		Socket s = null;
		
		try {
			s = new Socket(nodeHostname, nodePort);

			
		    try (
		    		PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		    		BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream()) );
		    ) {
		    	out.println( jObj.toString() );
		    	returnStr = in.readLine();
		    }
		    

		
		} catch (IOException e) {
			returnStr = "Error in send Transaction";
			e.printStackTrace();
		}
		
	    
		
		try { s.close(); } 
		catch (Exception e) {}

		
		return new JSONObject( returnStr );
	}
	
	public String getTransactionByIndex( String transactionHash, String guarantorHostname, int guarantorPort  )
	{
		
		String transactionData = "";
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("ActionToPerform", "readTransaction");
		jsonObject.put("BlockIndex", transactionHash);
		

		
	    try (
	    		Socket s = new Socket(guarantorHostname, guarantorPort);
	    		
	            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
	    		BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream()) );
	    ) {

	    	out.println( jsonObject.toString() );
	    	transactionData = in.readLine();
	    	
	    } catch (IOException e) {
			e.printStackTrace();
			transactionData = "Error";
		}
	    
		
		
		return transactionData;
	}
	
	/*
	 * 
	 * 
	 * END ACTIVITY DEFINITION
	 * 
	 * 
	 */
	
	
	
	
	
	

	
}
