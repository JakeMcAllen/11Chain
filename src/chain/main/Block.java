package chain.main;

import java.security.PublicKey;
import java.time.LocalDateTime;

import org.json.JSONObject;


/*
 *
 * 		BLOCK
 * 
 * 	
 * 
 * 
 * 	In the list: "listSCDataConnected" are stored info about smart contract connection to this block.
 * 		Every connection to this Block from other Block is decrypted in this file. 
 * 		The next block main chain connection is set to default in the file.
 * 		Every secondary block connection is describe in the file. Type and number of connection is described.
 * 				
 * 
 */
public class Block {
	
	// hash code of block
	private String hash;
	
	
	// Index and info
	private String index;
	
	private Boolean status; 
	
	private LocalDateTime timeData; 
	
	
	// Size of bite of data
	private int size; 
	
	
	// datas of block 
	private byte[] data;
	
	
	// Node connection
	private Node parentNode;
	
	
	// Block connection
	private Block previousBlock; 
	
	private Block nextBlock;
	
	
	// Gas 
	private int gas; 
	
	
	// Liste connesse 
	private JSONObject listSCDataConnected;
	
	
	
	
	
	
	
	public Block() {
		
		// TODO: set hash, index, data, time, size, parentNode, previousBlock
		
		// TODO: describe "listSCDataConnected" file.
		
	}
	
	
	
	
	
	public String getHash() 
	{
		return hash;
	}
	
	public PublicKey parentNodePublicKey() 
	{
		return parentNode.getPublicKey();
	}
	
	public void setPreviousBlock(Block block) 
	{	
		if (this.previousBlock == null) 
		{
			this.previousBlock = block;
		}
	}
	
	

	

	public static boolean HashIsBigger(String rt, String blockHash) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public Block getNextBlock () {
		
		return new Block();
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	public void encriptNode() {
		// TODO Auto-generated method stub
		
	}
	
	public int getBlockFirstNumber() {
		return 1;
	}
	
}
