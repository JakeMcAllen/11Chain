package chain.test;

import java.io.IOException;

//import java.io.IOException;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import chain.component.Transaction;
import chain.main.Guaranteer;
import chain.main.Node;
import chain.main.Users;

public class JTest {

//	@Test
//	public void NodeAndGuaranteer() {
//
//		// PORTS DEFINITION
//		int nodeIndex = 1;
//		String hostNode = "localhost";
//		int portNode = 8011;
//
//		String hostGuaranteer = "localhost";
//		int portGuaranteer = 8082;
//
//
//
//		// GUARANTEER
//		System.out.println("Start Guaranteer: ");
//		Guaranteer guaranteer = new Guaranteer( portGuaranteer );
//		System.err.println("\nGuaranteer is start with success on port: " + portGuaranteer);
//
//		// NODE
//		System.out.println("\nStart new Node: ");
//		Node node = new Node(hostNode, portNode, hostGuaranteer, portGuaranteer, nodeIndex );
//		System.err.println("Node is start with success on  port: " + portNode);
//
//		try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
//
//
//
//		// USERS
//		System.err.println("\nStart user 1");
//		
////		Users u = new Users("1", 100, 5, null, "Utente", "Utnt");
////		u.generateRSAKkeyPair();
////		u.setCurrentNode( node );
////		u.setCompanyIndex(11);
////		u.setCompanyName("BlueHorizon");
////		u.setNodeConnectionIndex( 11 );
////		try { u.saveUsers(); } 
////		catch (IOException e1) { e1.printStackTrace(); }
//		 
//		
//		Users u = new Users();
//		try { u.loadUsers(); } 
//		catch (Exception e1) { e1.printStackTrace(); }
//		
//		
//		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
//
//		System.err.println("User is start  with success.\n\n");
//
//
//
//
//
//
//
//
//		// TESTS 
//		for (int i=0; i<30; i++) {
//
//			JSONObject testTras = new JSONObject();
//			testTras.put("datas", "testInput di testo arbitrario");
//			testTras.put("index", i);
//
//			Transaction t = new Transaction(u, testTras);
//
//
//
//			// Send some data to node and read return transaction
////			u.sendTransaction( t );	
//			System.err.println("BlockIndex: " +  u.sendTransaction( t ).get("BlockIndex") + "\tIndex: " + i );
//
//
//			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
//		}
//
//		// TODO: Casi particolari da trattare
////		String transactionIndex = "8x5a"; 		// caso da trattare
////		String transactionIndex = "1x0a"; 		// caso da trattare
//		String transactionIndex = "2x2a";
//		
//		try {
//			
//			u.getTransactionByIndex( transactionIndex );
////			System.err.println("\n\nOutputDatas for transaction '" 
////					+ transactionIndex + "': \n\t\t" 
////					+ u.getTransactionByIndex( transactionIndex ).getJSONObject("transactionData")
////				);
//			
//		} catch (Exception e) { e.printStackTrace(); }
//
//
//
//		// CLOSING
//		try { guaranteer.setListenerIsActive( false ); }
//		catch (Exception e) { e.printStackTrace(); }
//		
//		node.setListenerIsActive( false );
//		//System.err.println("\n\nAll entities are closed");
//		
//	}
	
	
//	@Test
//	public void randomTest() {
//		
////		for (int i=0; i< 10; i++)
////			System.out.println( RandomString.newString() );
//    
//		
//		int[] ln = {1,2,3,4};
//		int[] ln2 = Arrays.copyOfRange(ln, 1, ln.length);
//		
//		for (int i : ln2)
//			System.out.print( i + " " );
//		
//	}

	
//	@Test
//	public void guaranteerTest() {
//		
//		int nodeIndex = 1;
//		String hostNode = "localhost";
//		int portNode = 8011;
//
//		String hostGuaranteer = "localhost";
//		int portGuaranteer = 8082;
//		
//		
//		// GUARANTEER START
//		Guaranteer guaranteer;
//		try { 
//			
//			System.out.println("GUARANTEER START");
//			
//			guaranteer = new Guaranteer();
//			
//			Users u = new Users();
//			try { u.loadUsers(); } 
//			catch (Exception e1) { e1.printStackTrace(); }
//			
//			Node node = new Node(hostNode, portNode, hostGuaranteer, portGuaranteer, nodeIndex );
//
//			
//			
//			String transactionIndex = "2x2a";
//			
//				
////				u.getTransactionByIndex( transactionIndex );
//				System.err.println("\n\nOutputDatas for transaction '" 
//						+ transactionIndex + "': \n\t\t" 
//						+ u.getTransactionByIndex( transactionIndex ).getJSONObject("transactionData")
//					);
//				
//			
//			
//			// GUARANTEER CLOSING
//			try { guaranteer.setListenerIsActive( false ); }
//			catch (Exception e) { e.printStackTrace(); }
//			
//			System.err.println("\n\nGuaranteer is close");
//		} 
//		catch (IOException | ParseException e1) { e1.printStackTrace(); }
//
//
//	}
    
	
	@Test 
	public void testBig() {
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
		
//		Users u = new Users("1", 100, 5, null, "Utente", "Utnt");
//		u.generateRSAKkeyPair();
//		u.setCurrentNode( node );
//		u.setCompanyIndex(11);
//		u.setCompanyName("BlueHorizon");
//		u.setNodeConnectionIndex( 11 );
//		try { u.saveUsers(); } 
//		catch (IOException e1) { e1.printStackTrace(); }
		 
		
		Users u = new Users();
		try { u.loadUsers(); } 
		catch (Exception e1) { e1.printStackTrace(); }
		
		
		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

		System.err.println("User is start  with success.\n\n");








		// TESTS 
		for (int i=0; i<30; i++) {

			JSONObject testTras = new JSONObject();
			testTras.put("datas", "testInput di testo arbitrario");
			testTras.put("index", i);

			Transaction t = new Transaction(u, testTras);



			// Send some data to node and read return transaction
//			u.sendTransaction( t );	
			System.err.println("BlockIndex: " +  u.sendTransaction( t ).get("BlockIndex") + "\tIndex: " + i );


			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
		}

		// TODO: Casi particolari da trattare
//		String transactionIndex = "8x5a"; 		// caso da trattare
//		String transactionIndex = "1x0a"; 		// caso da trattare
		String transactionIndex = "2x2a";
		
		try {
			
			u.getTransactionByIndex( transactionIndex );
//			System.err.println("\n\nOutputDatas for transaction '" 
//					+ transactionIndex + "': \n\t\t" 
//					+ u.getTransactionByIndex( transactionIndex ).getJSONObject("transactionData")
//				);
			
		} catch (Exception e) { e.printStackTrace(); }



		// CLOSING
		try { guaranteer.setListenerIsActive( false ); }
		catch (Exception e) { e.printStackTrace(); }
		
		try { node.setListenerIsActive( false ); } 
		catch (IOException e2) { e2.printStackTrace(); }
		//System.err.println("\n\nAll entities are closed");
		

		
		// GUARANTEER START
		Guaranteer guaranteer1;
		try { 
			
			System.out.println("GUARANTEER START");
			
			guaranteer1 = new Guaranteer();
			
			Users u2 = new Users();
			try { u2.loadUsers(); } 
			catch (Exception e1) { e1.printStackTrace(); }
			
			Node node1 = new Node(hostNode, portNode, hostGuaranteer, portGuaranteer, nodeIndex );

			
						
				
//				u2.getTransactionByIndex( transactionIndex );
				System.err.println("\n\nOutputDatas for transaction '" 
						+ transactionIndex + "': \n\t\t" 
						+ u2.getTransactionByIndex( transactionIndex ).getJSONObject("transactionData")
					);
				
			
			
			// GUARANTEER CLOSING
			try { guaranteer1.setListenerIsActive( false ); }
			catch (Exception e) { e.printStackTrace(); }
			node1.setListenerIsActive( false );

			System.err.println("\n\nGuaranteer is close");
		} 
		catch (IOException | ParseException e1) { e1.printStackTrace(); }


	}

}