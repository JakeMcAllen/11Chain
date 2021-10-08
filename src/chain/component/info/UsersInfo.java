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


	
	// Company info of worker
	private String companyName;
	private int companyId;
	private int companyNodeIndex;
	
	
	
	
	

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
			int permissions, String companyName, int companyId, int companyNodeIndex) {
		super();
		this.index = index;
		this.name = name;
		this.surname = surname;
		this.balance = balance;
		this.publicKey = publicKey;
		this.data = data;
		this.permissions = permissions;
		this.companyName = companyName;
		this.companyId = companyId;
		this.companyNodeIndex = companyNodeIndex;
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getCompanyNodeIndex() {
		return companyNodeIndex;
	}

	public void setCompanyNodeIndex(int companyNodeIndex) {
		this.companyNodeIndex = companyNodeIndex;
	}


	
	
	@Override
	public String toString() {
		return "UsersInfo [index=" + index + ", name=" + name + ", surname=" + surname + ", balance=" + balance
				+ ", publicKey=" + publicKey + ", data=" + Arrays.toString(data) + ", permissions=" + permissions
				+ ", companyName=" + companyName + ", companyId=" + companyId + ", companyNodeIndex=" + companyNodeIndex
				+ "]";
	}


}