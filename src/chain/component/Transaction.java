package chain.component;

import java.io.File;

import javax.xml.bind.DatatypeConverter;
import javax.xml.crypto.Data;

import org.json.JSONObject;

import chain.main.Users;

public class Transaction {
	
	
		
	private JSONObject data;
		
	private Users user;
	private String userID;
	
	

	
	public Transaction(String user, JSONObject data) {

		this.data = data;
		this.userID = user;
	}
	
	public Transaction(Users user, JSONObject data) {
		this(user.getIndex(), data);
		
		this.user = user;
	}
	
	public Transaction(Users user, String str) {
		this(str, new JSONObject( str ) );
	}
	
	public Transaction(JSONObject jObj) {
		this( jObj.get("userId").toString() , jObj );
	}	


	  
	
	
	/*
	 * 
	 * 	OPERATION ON TRANSACTION 
	 * 
	 */
	public void setData (String str) 
	{
		
		JSONObject jArr = new JSONObject();
		
		jArr.append("Type", "String" );
		jArr.append("Name", "String" );
		jArr.append("Lenght", str.length() );
		jArr.append("Owner", user.getData() );

		
		
		data.append("info", jArr);
		data.append("tipo", "String");
		data.append("data", str);
		
	}
	
	
	public void setObject(File file) 
	{
		
		JSONObject jArr = new JSONObject();
		
		jArr.append("Type", file.getName().substring( file.getName().lastIndexOf(".") ) );
		jArr.append("Name", file.getName() );
		jArr.append("Lenght", file.length() );
		jArr.append("Owner", user.getData() );
		
		
		
		data.append("info", jArr);
		data.append("tipo", "file");
		data.append("data", file);
		
	}
	
	
	/*
	 * 
	 * GET
	 * 
	 */
	public Users getUser() 
	{
		return user;
	}
	
	public void serUser(Users user)
	{
		this.user = user;
	}
	
	public String getUserID() { 
		return this.userID;
	}
	
	public JSONObject getTransaction() 
	{
		return data;
	}
	
	public int getLenght() 
	{
		return data.toString().length();
	}
	
	public byte[] getByte() 
	{
		return this.data.toString().getBytes() ;
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
		
		jObj.append("data", this.data);
 		jObj.append("userId", user.getIndex() );


		return jObj;
		
	}

	// get transaction object and create Transaction
	public static Transaction ObjFromJSON(JSONObject jObj) {
		
		
		JSONObject dataJ = (JSONObject) jObj.get("data");
		String userId = (String) jObj.get("userId");
		
		
		return new Transaction(userId, dataJ);
	}

	@Override
	public String toString() {
		return "Transaction [data=" + data + ", user=" + user + ", userID=" + userID + "]";
	}
	
	
}
