package chain.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Scanner;

import org.json.JSONObject;

import chain.component.Transaction;


public class Users {
	
	
	private String index;
	
	// Keys
	private PrivateKey privateKey = null;
	private PublicKey publicKey = null;
	
	
	// Balance
	private long balance;
	
	// Permissions   from 0 to 5   ( from Low to Hight )
	private int permissions;
	
	// data
	private byte[] data;
 	
	// Node Adders
	
	
	
	
	
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




















	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
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

	

	
	
	
	
	/*
	 * 
	 * 
	 * START ACTIVITY DEFINITION
	 * 
	 * 
	 */
	
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
	}
	
	// Send transaction
	public String sendTransaction( Transaction t, String guarantorHostname, int guarantorPort ) 
	{
		
		String returnStr = "";
		
		JSONObject jObj = new JSONObject();
		jObj.put("ActionToPerform", "postTransactionInPool");
		jObj.put("Transaction", t.toJObj());
		Socket s = null;
		
		try {
			s = new Socket(guarantorHostname, guarantorPort);

			
		    try (
		    		PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		    		BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream()) );
		    ) {
		    	// Errore
		    	out.println( jObj.toString() );
		    	returnStr = in.readLine();
		    	
				System.out.println("----------KEYS CREATION END----------" + "\n" + 
						"Private key: " + this.getPrivateKey() + "\n" + 
						"Public key: " + this.getPublicKey() + "\n" + 
						"----------KEYS CREATION START----------" );
		    }
		    

		
		} catch (IOException e) {
			returnStr = "Error in send Transaction";
			e.printStackTrace();
		}
		
	    
		
		try { s.close(); } 
		catch (Exception e) {}

		
		return returnStr;
	}
	
	private void getTransaction( String transactionHash, String guarantorHostname, int guarantorPort  )
	{
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("ActionToPerform", "readTransaction");
		jsonObject.put("BlockHash", transactionHash);
		
		
		try {
			
			Socket s = new Socket(guarantorHostname, guarantorPort);

			OutputStream outputStream = s.getOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());
			
			objectOutputStream.writeObject( jsonObject );
			System.out.println("Transaction sent.");
			System.out.println("Transaction index: " + inStream.readUTF() );
			
			
			s.close();
			objectOutputStream.close();
			inStream.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * 
	 * 
	 * END ACTIVITY DEFINITION
	 * 
	 * 
	 */
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		boolean isRunning = true;
		
		
		try {
			
			// TODO: load user from file
			Users u = new Users();

			
			
			Scanner s = new Scanner(System.in);
			
			
			while ( isRunning ) {
				
			    String userInput = s.next();
			    
			    
			    // TODO: Add new actions to perform
			    switch (userInput) {
			    
				case "stop": {
					
					isRunning = false;
					break;
				}
				
				case "tctn": {
					
					// TODO: Test connection to node
					break;
				}
				
				case "createKey": {
					
					if ( u.getPrivateKey() != null )
					{
						u.generateRSAKkeyPair();
					}
					
					System.out.println("----------KEYS CREATION END----------" + "\n" + 
							"Private key: " + u.getPrivateKey() + "\n" + 
							"Public key: " + u.getPublicKey() + "\n" + 
							"----------KEYS CREATION START----------" );
					
					break;
				}
				
				case "sendTransaction":
					
				    String str = s.next();
				    if ( str == "" ) 
				    {
					    Transaction t = new Transaction(u, str);
					    u.sendTransaction(t, "localhost", 8081);	
				    }
					break;
									
				case "getTransaction": {

				    String transactionHash = s.next();
				    u.getTransaction(transactionHash, "localhost", 8081);
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
