package chain.component.info;

import java.security.PublicKey;
import java.util.Arrays;

/**
 * 	Contain all info abot a user
 * 
 * @author giorg
 *
 */
public class UsersInfo {

	// worker info
	private String index;
	private String name;
	private String surname;
	
	private long balance;
	private PublicKey publicKey = null;

	
	// Final data about user
	private byte[] data;


	


	// Permissions   from 0 to 5   ( from Low to Height )
	private int permissions;

	

	// Info about node connection
	private int nodeConnectionIndex;
	private String nodeConnectionHostName;
	private int nodeConnectionPort;
	
	
	
	
	// Company info
	private String CompanyName;
	private int CompanyIndex;
	private int CompanyNodeIndex;
	
	
	
	

	public UsersInfo() {
	}

	public UsersInfo(String index, String name, PublicKey publicKey, long balance,
			int permissions, byte[] data) {
		super();
		this.index = index;
		this.name = name;
		this.publicKey = publicKey;
		this.balance = balance;
		this.permissions = permissions;
		this.data = data;
	}

	public UsersInfo(String index, String name, String surname, long balance, PublicKey publicKey, byte[] data,
			int permissions, int nodeConnectionIndex, String nodeConnectionHostName, int nodeConnectionPort,
			String companyName, int companyIndex, int companyNodeIndex) {
		super();
		this.index = index;
		this.name = name;
		this.surname = surname;
		this.balance = balance;
		this.publicKey = publicKey;
		this.data = data;
		this.permissions = permissions;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
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

	
	
	
	@Override
	public String toString() {
		return "UsersInfo [index=" + index + ", name=" + name + ", surname=" + surname + ", balance=" + balance
				+ ", publicKey=" + publicKey + ", data=" + Arrays.toString(data) + ", permissions=" + permissions
				+ ", nodeConnectionIndex=" + nodeConnectionIndex + ", nodeConnectionHostName=" + nodeConnectionHostName
				+ ", nodeConnectionPort=" + nodeConnectionPort + ", CompanyName=" + CompanyName + ", CompanyIndex="
				+ CompanyIndex + ", CompanyNodeIndex=" + CompanyNodeIndex + "]";
	}
	
}