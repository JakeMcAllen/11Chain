package chain.test;

import chain.component.Node;
import chain.component.Transaction;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;


import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;



public class GenericTest {



	int iz = 0;
	
	


	public static void main(String[] args) throws Exception {

		// Initial node test
		testNodeStartUp();

		// test oabout String and Byte
		// testString();

		// Transaction example
		// transacrionTest();

		// Timer action
		// GenericTest gt = new GenericTest();
		
		
		// JsonObject 
		// jsonObjTest();
		
		
		
		// Scanner in = new Scanner(System.in);

		// System.out.println("in: " + in );
		
		
	}



	private static void jsonObjTest() {

		String js = " {	\"name\" : \"Ronaldo\",	\"nickname\" : \"Sam\", \"id\" : 312, \"age\" : 21, \"height\" : 163, \"lastOverScore\" : [4, 1, 6, 6, 2, 1] } ";

		
		String str = "[{\"No\":\"17\",\"Name\":\"Andrew\"},{\"No\":\"18\",\"Name\":\"Peter\"}, {\"No\":\"19\",\"Name\":\"Tom\"}]";  
		
		
		System.err.println("arr");
		JSONArray array = new JSONArray(str);  
		
		for(int i=0; i < array.length(); i++)   
		{  
			JSONObject object = array.getJSONObject(i);  
			System.out.println(object.getString("No"));  
			System.out.println(object.getString("Name"));  
		}    
		
		System.err.println("obj:");
		
		JSONObject obj = new JSONObject(js);  
		
		System.out.println(obj.get("name"));
		System.out.println(obj.get("id"));
		System.out.println(obj.get("lastOverScore"));
		
		
	}



	public GenericTest() {
		
		// timer test
        /*
		Timer timer = new Timer();
        TimerTask task = new GenericTest.Helper(this);
        
        timer.schedule(task, 0, 1000);
		*/
		
		
	}	
	
	
	


	public static void transacrionTest() {

		Integer seconds = 5;

		Timer timer = new Timer();
		long msec = 1000;
		timer.scheduleAtFixedRate( new MyTask(), 0*msec, seconds.intValue()*msec );

		System.out.println("Timer is start");

	}



	
	
	public static void testString() {
		Transaction tc = new Transaction("", null);

		
		tc.setData("TestString");
		
		
		System.out.println("tc: " + tc.getSByte() + " - " + tc.getTransaction() + " - - -  " + tc.getByte());


		for (byte b : tc.getByte()) {
			System.out.print( b + " " );
		}

		
		System.out.println( "\nl1: " + tc.getLenght() );

	}



	public static void testNodeStartUp () throws Exception {


		Node n = new Node(8081, "localhost", 8080);
		String plainText = "test";



		System.out.println( "Public Key is: \n\t" + n.getPublicKey() );
		// System.out.println( "\nPrivate Key is: \n\t" + n.privateKey );


		byte[] cipherText = n.dataEncryption( plainText );



		System.out.println( "\n\nThe encripted test is: " + DatatypeConverter.printHexBinary(cipherText));
		System.out.println( "The decrypted text is: " + n.dataDecryption( cipherText ) );


	}


	
	public void printData(int i) {
		this.iz++;
		
		System.out.println( ":::>  " + this.iz + " - " + i );
	}
	
	
	private class Helper extends TimerTask {
		
		GenericTest t;
		
		
	    public static int i = 0;
	    public Helper(GenericTest genericTest) {
	    	t = genericTest;
	    }
	    
		public void run()
	    {
	        System.out.println("Timer ran " + ++i);
	        printData(i);
	    }
		
	}
	
}


class MyTask extends TimerTask {

	public void run() {
		System.out.println("Hello");
	}

}


