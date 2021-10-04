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
		int portNode = 8011;

		String hostGuaranteer = "localhost";
		int portGuaranteer = 8082;



		// GUARANTEER
		System.out.println("Start Guaranteer: ");
		Guaranteer guaranteer = new Guaranteer( portGuaranteer );
		System.err.println("\nGuaranteer is start with success on port: " + portGuaranteer);

		// NODE
		System.out.println("\nStart new Node: ");
		Node node = new Node(hostNode, portNode, hostGuaranteer, portGuaranteer, 1 );
		System.err.println("Node is start with success on  port: " + portNode);

		try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }



		// USERS
		System.err.println("\nStart user 1");
		Users u = new Users("1", 100, 5);
		u.generateRSAKkeyPair();

		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

		System.err.println("User is start  with success.\n\n");








		// TESTS 
		// create a transaction


		for (int i=0; i<30; i++) {

			JSONObject testTras = new JSONObject();
			testTras.put("data", "testInput di testo arbitrario");
			testTras.put("index", i);

			Transaction t = new Transaction(u, testTras);
//			System.out.println("Transaction: " + t);

			
			
			
			// send some data to node
			JSONObject transactionIndex = new JSONObject( u.sendTransaction( t, hostNode, portNode) );	
			
			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

			System.err.println("\nBlockIndex: " +  transactionIndex.get("BlockIndex") );
			
		}

//		try 
//		{ 
//			String getDatas = u.getTransactionByIndex("1x1a" ,hostNode, portNode);
//			
//			System.out.println(\n\n"OutputDatas: " + getDatas );
//		} 
//		catch (Exception e) { e.printStackTrace(); }
		


		// CLOSING
		guaranteer.setListenerIsActive( false );
		node.setListenerIsActive( false );
		System.err.println("All entities are closed");

	}

}
