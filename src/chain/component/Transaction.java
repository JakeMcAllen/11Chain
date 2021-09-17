package chain.component;

import java.util.Timer;
import java.util.TimerTask;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

public class Transaction {
	
	private JSONObject transaction;
	
	
	
	public Transaction() {
		transaction = new JSONObject();
		
		// inserisco i dati standard di una transazione
		
	}
	
	
	

	  
	
	
	/*
	 * 
	 * 	OPERATION ON TRANSACTION 
	 * 
	 */
	public void add(String key, Object obj ) 
	{
		transaction.append(key, obj);
	}
	
	public JSONObject getTransaction() 
	{
		return transaction;
	}
	
	
	
	/*
	 * 
	 * 	GET DATA FROM TRANSACTION
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

	public String getSByte() 
	{
		return DatatypeConverter.printHexBinary( getByte() );
	}
	
	public int getBLenght() {
		return getByte().length;
	}
	
}
