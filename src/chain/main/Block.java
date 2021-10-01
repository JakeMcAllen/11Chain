package chain.main;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
	
	// Hash code of block and data of block ( Transactions )
	private String hash;
	
	private byte[] data;
	
	
	
	// Index and info
	private String index;
	
	private Boolean status; 
	private LocalDateTime timeData; 
	
	
	
	
	// Size of bite of data
	private int size; 
	
	// Gas 
	private int gas; 
	
	
	// Node
	private String parentNode;
	
	
	
	// Liste connesse info 
	private JSONObject listSCDataConnected;
	
	
	private String parentHash;
	
	private boolean hasNext = false;
	private int numberOfSiblingBlock = 0;

	
	
	
	// Link to Parent Block, links to previus block and list of link of next block to this
	private Block parentBlock;
	
	private Block nextBlock;
	private List<Block> siblingBlock;
	
	
	
	
	
	
	
	
	
	public Block() {}	
	
	public Block(Block parentBlock, byte[] data) 
	{		
		this.parentBlock = parentBlock;
		siblingBlock = new ArrayList<Block>();
	}
	
	public Block(String hash, byte[] data, String index, Boolean status, LocalDateTime timeData, int size, int gas,
			String parentNode, JSONObject listSCDataConnected, String parentHash, boolean hasNext, int numberOfSiblingBlock,
			Block parentBlock, Block nextBlock, List<Block> siblingBlock) 
	{
		super();
		this.hash = hash;
		this.data = data;
		this.index = index;
		this.status = status;
		this.timeData = timeData;
		this.size = size;
		this.gas = gas;
		this.parentNode = parentNode;
		this.listSCDataConnected = listSCDataConnected;
		this.parentHash = parentHash;
		this.hasNext = hasNext;
		this.numberOfSiblingBlock = numberOfSiblingBlock;
		this.parentBlock = parentBlock;
		this.nextBlock = nextBlock;
		this.siblingBlock = siblingBlock;
	}
	

	
	
	public String getHash() 
	{
		return hash;
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public LocalDateTime getTimeData() {
		return timeData;
	}

	public void setTimeData(LocalDateTime timeData) {
		this.timeData = timeData;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getGas() {
		return gas;
	}

	public void setGas(int gas) {
		this.gas = gas;
	}
	
	public String getParentNode() {
		return parentNode;
	}

	public void setParentNode(String parentNode) {
		this.parentNode = parentNode;
	}

	public JSONObject getListSCDataConnected() {
		return listSCDataConnected;
	}

	public void setListSCDataConnected(JSONObject listSCDataConnected) {
		this.listSCDataConnected = listSCDataConnected;
	}

	public String getParentHash() {
		return parentHash;
	}

	public void setParentHash(String parentHash) {
		this.parentHash = parentHash;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public int getNumberOfSiblingBlock() {
		return numberOfSiblingBlock;
	}

	public void setNumberOfSiblingBlock(int numberOfSiblingBlock) {
		this.numberOfSiblingBlock = numberOfSiblingBlock;
	}

	public Block getParentBlock() {
		return parentBlock;
	}

	public void setParentBlock(Block parentBlock) {
		this.parentBlock = parentBlock;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setSiblingBlock(List<Block> siblingBlock) {
		this.siblingBlock = siblingBlock;
	}

	public void setNextBlock( Block b ) {
		if ( nextBlock != null ) nextBlock = b;
	}
	
	public Block getNextBlock() {
		return nextBlock;
	}
	
	public List<Block> getSiblingBlock () {
		return siblingBlock;
	}

	public void addSiblingBlock(Block nb) {
		siblingBlock.add(nb);	
	}
	
	public int SiblingBlockNumber () {
		return numberOfSiblingBlock;
	}
	
	public boolean hasNextBlock() {
		return hasNext;
	}
	
	public String getParentBlockHash() {
		return parentHash;
	}
	
	
	
	
	
	

	
	
	public static Block generateBlockFromJSON( JSONObject jObj ) 
	{	
		Block b = new Block();
		
		
		if ( jObj.has("data") ) b.setData( (byte[]) jObj.get( "data" ) );
		if ( jObj.has("gas") ) b.setGas( jObj.getInt("gas") );
		if ( jObj.has("hash") ) b.setHash( jObj.getString("hash") );
		
		if ( jObj.has("hasNext") ) b.setHasNext( jObj.getBoolean("hasNext") );
		if ( jObj.has("index") ) b.setIndex( jObj.getString("index") );
		if ( jObj.has("numberOfSiblingBlock") ) b.setNumberOfSiblingBlock( jObj.getInt("numberOfSiblingBlock") );
		if ( jObj.has("parentHash") ) b.setParentHash( jObj.getString("parentHash") );
		if ( jObj.has("size") ) b.setSize( jObj.getInt("size") );
		if ( jObj.has("status") ) b.setStatus( jObj.getBoolean("status") );
		if ( jObj.has("timeData") ) b.setTimeData( LocalDateTime.parse( jObj.getString("timeData") ) );
		
		
		
		return b;
	}
	
	
	
	
	
	public JSONObject toJSON() 
	{	
		JSONObject jObj = new JSONObject();
		
		jObj.put("data", this.data);
		jObj.put("gas", this.gas);
		jObj.put("hash", this.hash);
		jObj.put("hasNext", this.hasNext);
		jObj.put("index", this.index);
		jObj.put("numberOfSiblingBlock", this.numberOfSiblingBlock);
		jObj.put("parentHash", this.parentHash);
		jObj.put("parentNode", this.parentNode);
		jObj.put("size", this.size);
		jObj.put("status", this.status);
		jObj.put("timeData", this.timeData);
		
		
		return jObj;
	}
	


	
	
	
	
	@Override
	public String toString() {
		return "Block [hash=" + hash + ", data=" + Arrays.toString(data) + ", index=" + index + ", status=" + status
				+ ", timeData=" + timeData + ", size=" + size + ", gas=" + gas + ", parentNode=" + parentNode
				+ ", listSCDataConnected=" + listSCDataConnected + ", parentHash=" + parentHash + ", hasNext=" + hasNext
				+ ", numberOfSiblingBlock=" + numberOfSiblingBlock + ", parentBlock=" + parentBlock + ", nextBlock="
				+ nextBlock + ", siblingBlock=" + siblingBlock + "]";
	}

	
	
	
	
	// TODO
	public byte[] encriptNode( PrivateKey privateKey ) 
	{
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init( Cipher.ENCRYPT_MODE, privateKey);
			return cipher.doFinal( this.data );
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	// TODO
	public int getBlockFirstNumber() 
	{
		return Integer.parseInt( index.substring(0, 1) );
	}


	
	public boolean blockIsFromMainChian() 
	{
		return index.length() == 2 ? true : false;
	}
	
	
}