package chain.component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

import chain.main.Users;

public class Transaction {
	
	
		
	private JSONObject transactionData;
	private JSONObject userD;
	private JSONObject node;
	
	
	

	public Transaction() {
		userD = new JSONObject();
		node = new JSONObject();
	}
	
	
	public Transaction(String user, JSONObject data) {
		this();
		
		this.transactionData = data;
	}
	
	public Transaction(Users user, JSONObject data) {
		this(user.getIndex(), data);
		
		
		
		this.userD.put("index", user.getIndex() );
		this.userD.put("name", user.getName() );
		this.userD.put("surname", user.getSurname() );
		this.userD.put("balance", user.getBalance() );
		this.userD.put("publicKey", user.getPublicKey() );
		this.userD.put("data", user.getData() );
		this.userD.put("permission", user.getPermissions() );
		this.userD.put("CompanyName", user.getCompanyName() );
		this.userD.put("CompanyIndex", user.getCompanyIndex() );
		this.userD.put("CompanyNodeIndex", user.getCompanyNodeIndex() );
		
		
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
		this.userD = user;
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
			arrayOutputStream.write( this.userD.toString().getBytes() );
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
	
	
	
	
	
	/*
	 * 
	 * TO and FROM JSONObject
	 * 
	 */
	public JSONObject toJObj() {
		JSONObject jObj = new JSONObject();
		
		jObj.put("transactionData", this.transactionData);
 		jObj.put("user", this.userD);
 		jObj.put("node", this.node);

		return jObj;
		
	}

	// get transaction object and create Transaction
	public static Transaction ObjFromJSON(JSONObject jObj) {
		
		
		JSONObject dataJ = jObj.getJSONObject("transactionData");
		JSONObject userJ = jObj.getJSONObject("user");
		JSONObject nodeJ = jObj.getJSONObject("node");		 
		
		
		
		return new Transaction(dataJ, userJ, nodeJ);
	}

	public void setNodeDeestination(String nodeHostName, int nodePort, int nodeIndex) {
		
		String dateTime = LocalDateTime.now().format( DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss") );
		
		this.node.put("HostName", nodeHostName);
		this.node.put("Port", nodePort);
		this.node.put("Index", nodeIndex);
		this.node.put("dataSending", dateTime );
		
	}
	
	


	

	

	
	

	@Override
	public String toString() {
		return "Transaction [transactionData=" + transactionData + ", userD=" + userD + ", node=" + node + "]";
	}

}