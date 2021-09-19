package chain.component;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

public class SmartContract {
	
	private JSONObject transaction;
	
	
	
	public SmartContract() {
		transaction = new JSONObject();
		
	}
	
	
	
	/*
	 * 
	 * 	Transaction action 
	 * 
	 */
	public void addData(String key, Object obj ) 
	{
		transaction.append(key, obj);
	}
	
	public JSONObject getTransaction() 
	{
		return transaction;
	}
	
	
	
	/*
	 * 
	 * 	Get length
	 * 
	 */
	public int getLenght() 
	{
		return transaction.toString().length();
	}
	
	public byte[] getByte() 
	{
		return transaction.toString().getBytes();
	}

}
