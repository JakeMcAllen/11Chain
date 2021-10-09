package chain.test;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
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
	//		Users u = new Users("1", 100, 5);
	//		u.generateRSAKkeyPair();
	//		u.setCurrentNode( node );
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
	//		// create a transaction
	//
	//
	//		for (int i=0; i<30; i++) {
	//
	//			JSONObject testTras = new JSONObject();
	//			testTras.put("datas", "testInput di testo arbitrario");
	//			testTras.put("index", i);
	//
	//			Transaction t = new Transaction(u, testTras);
	////			System.out.println("Transaction: " + t);
	//
	//			
	//			
	//			// Send some data to node and read return transaction
	//			JSONObject responseTransaction = u.sendTransaction( t, hostNode, portNode, nodeIndex );	
	//			System.err.println("BlockIndex: " +  responseTransaction.get("BlockIndex") + "\tIndex: " + i );
	//			
	//			
	//			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
	//		}
	//
	//		try 
	//		{ 
	//			String getDatas = u.getTransactionByIndex("1x5a" ,hostNode, portNode);
	//			
	//			System.err.println("\n\nOutputDatas: " + getDatas );
	//		} 
	//		catch (Exception e) { e.printStackTrace(); }
	//		
	//
	//
	//		// CLOSING
	//		guaranteer.setListenerIsActive( false );
	//		node.setListenerIsActive( false );
	//		System.err.println("\n\nAll entities are closed");
	//
	//	}

	@Test 
	public void timetest() {
		
	    TimerTask task = new MyTimeTask();

	    
		
		
		
		Calendar calendar = Calendar.getInstance();
		int period = 100000; //10secs
		
		
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);		
	    
	    ( new Timer() ).schedule(task, calendar.getTime(), period );
		
	    
	    
	    
	    
		// print out tomorrow's date
		System.out.println("tomorrow: " + calendar.getTime());

	    
	}

}