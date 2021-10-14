package chain.component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

import chain.extraClasses.zipKey;
import chain.main.Users;

public class Transaction {
	
	
		
	private JSONObject transactionData;
	private JSONObject user;
	private JSONObject node;
	
	
	

	public Transaction() {
		user = new JSONObject();
		node = new JSONObject();
	}
	
	
	public Transaction(String user, JSONObject data) {
		this();
		
		this.transactionData = data;
	}
	
	public Transaction(Users user, JSONObject data) {
		this(user.getIndex(), data);
		
		
		
		this.user.put("index", user.getIndex() );
		this.user.put("name", user.getName() );
		this.user.put("surname", user.getSurname() );
		this.user.put("balance", user.getBalance() );
		this.user.put("publicKey", zipKey.zipPublicKey( user.getPublicKey() ) );
		this.user.put("data", user.getData() );
		this.user.put("permission", user.getPermissions() );
		this.user.put("CompanyName", user.getCompanyName() );
		this.user.put("CompanyIndex", user.getCompanyIndex() );
		this.user.put("CompanyNodeIndex", user.getCompanyNodeIndex() );
		
		
	}
	
	public Transaction(Users user, String str) {
		this(str, new JSONObject( str ) );
	}
	
	public Transaction(JSONObject jObj) {
		this(
				jObj.getJSONObject("transactionData"),
				jObj.getJSONObject("user"),
				jObj.getJSONObject("node")
			);
	}	

	public Transaction(JSONObject data, JSONObject user, JSONObject node) {
		this.transactionData = data;
		this.user = user;
		this.node = node;
		
	}
	  
	
	
	
	
	
	
	
	/*
	 * 
	 * 	OPERATION ON TRANSACTION 
	 * 
	 */
	
	
	/*
	 * 
	 * GET
	 * 
	 */
	
	public JSONObject getTransaction() 
	{
		return transactionData;
	}
	
	public int getLenght() 
	{
		return transactionData.toString().length();
	}
	
	public byte[] getByte() 
	{
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		
		try {
			arrayOutputStream.write( this.transactionData.toString().getBytes() );
			arrayOutputStream.write( this.user.toString().getBytes() );
			arrayOutputStream.write( this.node.toString().getBytes() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arrayOutputStream.toByteArray();
	}

	public String getSByte() 
	{
		return DatatypeConverter.printHexBinary( getByte() );
	}
	
	public JSONObject getTransactionData() {
		return transactionData;
	}

	public void setTransactionData(JSONObject transactionData) {
		this.transactionData = transactionData;
	}

	public JSONObject getUser() {
		return user;
	}

	public void setUser(JSONObject user) {
		this.user = user;
	}

	public JSONObject getNode() {
		return node;
	}

	public void setNode(JSONObject node) {
		this.node = node;
	}


	
	
	
	
	
	
	/*
	 * 
	 * TO and FROM JSONObject
	 * 
	 */
	public JSONObject toJObj() {
		JSONObject jObj = new JSONObject();
		
		jObj.put("transactionData", this.transactionData);
 		jObj.put("user", this.user);
 		jObj.put("node", this.node);
 		jObj.put("transactionDataLength", this.transactionData.toString().length() );
 		 		
		return jObj;
		
	}

	// get transaction object and create Transaction
	public static Transaction ObjFromJSON(JSONObject jObj) {
		
		JSONObject dataJ = jObj.getJSONObject("transactionData");
		JSONObject userJ = jObj.getJSONObject("user");
		JSONObject nodeJ = jObj.getJSONObject("node");		 
		
		
		
		return new Transaction(dataJ, userJ, nodeJ);
	}

	public void setNodeDeestination(String nodeHostName, int nodePort, int nodeConnectionIndex) {
		
		String dateTime = LocalDateTime.now().format( DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss") );
		
		this.node.put("HostName", nodeHostName);
		this.node.put("Port", nodePort);
		this.node.put("Index", nodeConnectionIndex);
		this.node.put("dataSending", dateTime );
		
	}
	
	


	

	

	
	

	@Override
	public String toString() {
		return "Transaction [transactionData=" + transactionData + ", user=" + user + ", node=" + node + "]";
	}

}