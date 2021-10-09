package chain.test;

import org.json.JSONObject;
import org.junit.Test;

import chain.component.Transaction;
import chain.main.Guaranteer;
import chain.main.Node;
import chain.main.Users;

public class JTest {

	@Test
	public void NodeAndGuaranteer() {

		// PORTS DEFINITION
		int nodeIndex = 1;
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
		Node node = new Node(hostNode, portNode, hostGuaranteer, portGuaranteer, nodeIndex );
		System.err.println("Node is start with success on  port: " + portNode);

		try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }



		// USERS
		System.err.println("\nStart user 1");
		Users u = new Users("1", 100, 5, null, "Utente", "Utnt");
		
		u.generateRSAKkeyPair();
		u.setCurrentNode( node );

		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

		System.err.println("User is start  with success.\n\n");








		// TESTS 
		for (int i=0; i<30; i++) {

			JSONObject testTras = new JSONObject();
			testTras.put("datas", "testInput di testo arbitrario");
			testTras.put("index", i);

			Transaction t = new Transaction(u, testTras);
			//			System.out.println("Transaction: " + t);



			// Send some data to node and read return transaction
			JSONObject responseTransaction = u.sendTransaction( t, hostNode, portNode, nodeIndex );	
			System.err.println("BlockIndex: " +  responseTransaction.get("BlockIndex") + "\tIndex: " + i );


			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
		}

		try 
		{ 
			String getDatas = u.getTransactionByIndex("1x5a" ,hostNode, portNode);

			System.err.println("\n\nOutputDatas: " + getDatas );
		} 
		catch (Exception e) { e.printStackTrace(); }



		// CLOSING
		guaranteer.setListenerIsActive( false );
		node.setListenerIsActive( false );
		System.err.println("\n\nAll entities are closed");

	}


}