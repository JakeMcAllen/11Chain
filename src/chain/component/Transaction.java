package chain.component;

import java.io.File;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

public class Transaction {
	
	
		
	private JSONObject data;
	
	private Users user;

	
	
	
	public Transaction(Users user, JSONObject data) {

		this.user = user;
		this.data = data;

		
				
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
		jArr.append("Lenght", str.length() );
		jArr.append("Owner", user.getBaseData() );

		
		
		data.append("info", "String");
		data.append("tipo", "String");
		data.append("file", str);
		
	}
	
	public void setObject(File file) 
	{
		
		JSONObject jArr = new JSONObject();
		
		jArr.append("Type", file.getName().substring( file.getName().lastIndexOf(".") ) );
		jArr.append("Name", file.getName() );
		jArr.append("Lenght", file.length() );
		jArr.append("Owner", user.getBaseData() );
		
		
		
		data.append("info", jArr);
		data.append("tipo", "file");
		data.append("file", file);
		
	}
	
	
	
	public Users getUser() 
	{
		return user;
	}
	
	public JSONObject getTransaction() 
	{
		return data;
	}
	
	public void serUser(Users user)
	{
		this.user = user;
	}
	
	
	
	
	
	
	/*
	 * 
	 * 	GET DATA FROM TRANSACTION
	 * 
	 */
	public int getLenght() 
	{
		return data.toString().length();
	}
	
	public byte[] getByte() 
	{
		return data.toString().getBytes();
	}

	public String getSByte() 
	{
		return DatatypeConverter.printHexBinary( getByte() );
	}
	
	public int getBLenght() {
		return getByte().length;
	}






	// get transaction object and create Transaction
	public static Transaction ObjFromJSON(JSONObject jsonObject) {
		return null;
	}
	
}
