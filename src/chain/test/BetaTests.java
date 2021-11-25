package chain.test;

import org.junit.Test;


/*
 * Example code:
 * 
 * 
 * 
 * 	mapping (Item::String) counter;
 * 	mapping (List::String) pop1;
 * 
 * 	contract p (Number i1, Number i2, Number i3) {
 *			Number x = 10 + i1 + i2 + i3; 
 *			print(x + 10);
 *			counter = x + 10; 
 *
 *			pop1.pop(1);
 *	}
 * 
 */


public class BetaTests {

	@Test
	public void BetaTestSC() {
		String inputString = "<404, mapping>,<40>,<272, Item>,<273, ::>,<270, string>,<41>,<257, counter>,<59>,<404, mapping>,<40>,<271, List>,<273, ::>,<270, string>,<41>,<257, pop1>,<59>,<100, contract>,<257, p>,<40>,<270, number>,<257, i1>,<44>,<270, number>,<257, i2>,<44>,<270, number>,<257, i3>,<41>,<123>,<270, number>,<257, x>,<61>,<256, 10>,<43>,<257, i1>,<43>,<257, i2>,<43>,<257, i3>,<59>,<403, print>,<40>,<257, x>,<43>,<256, 10>,<41>,<59>,<257, counter>,<61>,<257, x>,<43>,<256, 10>,<59>,<125>,<-1>";

		
		// Input parameter to add at function !!!!! 
		// String rsp = new SCExecutor().executeContract("p",inputString);
	}
	
	
	@Test
	public void generalTest() {

		String s = Character.toString( (char) 125 ); 
		
		System.out.println( s );
		
		
		if (  3 + 4 == 82 - 1 ) {
			System.out.println("Ciao ");
		} else {
			System.err.println("Bhoooo " + (3 + 4 == 10 - 1) );
		}
		
	}
	
	
}
