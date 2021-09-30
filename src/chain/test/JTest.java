package chain.test;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

import chain.component.Node;
import chain.component.Transaction;
import chain.main.Guaranteer;
import chain.main.Users;

public class JTest {

	@Test
	public void NodeAndGuaranteer() {
		
		// PORTS DEFINITION
		String hostNode = "localhost";
		int portNode = 8083;
		
		String hostGuaranteer = "localhost";
		int portGuaranteer = 8084;

		
		
		// GUARANTEER
		System.out.println("Start Guaranteer: ");
		Guaranteer guaranteer = new Guaranteer( portGuaranteer );
		System.err.println("\nGuaranteer is start with success on port: " + portGuaranteer);
		
		// NODE
		System.out.println("Start new Node: ");
		Node node = new Node( portNode, hostGuaranteer, portGuaranteer );
		System.err.println("Node is start with success on  port: " + portNode);
        
		try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

		
		
		// USERS
		Users u = new Users("1", 100, 5);
		u.generateRSAKkeyPair();
		System.err.println("User is start  with success.");
		
	    
		
	    
	    
		
		// TESTS 
		// create a transaction
		JSONObject testTras = new JSONObject();
		testTras.put("data", "testInput di testo arbitrario");
		
	    Transaction t = new Transaction(u, testTras);
	    System.out.println("t: " + t);
	    
	    
	    // send some data to node
	    String transactionIndex = u.sendTransaction( t, hostNode, portNode);	
	    System.err.println("transactionIndex: " + transactionIndex);
		
	    
		
	    
	    
		
		// CLOSING
		guaranteer.setListenerIsActive( false );
		node.setListenerIsActive( false );
		System.err.println("All entities are closed");
		
	}

}
