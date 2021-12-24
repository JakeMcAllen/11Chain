package chain.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import chain.SC.execution.SCExecuter;


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

		String str = "LDC 10;ISTORE 1;IADD;LDC 10;IADD;IADD;IADD;LDC 1;ILOAD 6;L2;GOTO L2;INVOKEVIRTUAL PRINT;LDC 0;L4;GOTO L2;INVOKEVIRTUAL PRINT;ILOAD 6;L3;GOTO L4;INVOKEVIRTUAL CONTR L3;IF_ICMPLT;LDC 0;ILOAD 6;L1;GOTO L2;INVOKEVIRTUAL PRINT;IADD;LDC 10;ILOAD 6;L0;GOTO L1;INVOKEVIRTUAL CONTR L0;IF_ICMPGT;LDC 0;ILOAD 6;INVOKEVIRTUAL PRINT;IADD;LDC 10;ILOAD 6;ISTORE 6;IADD;ILOAD 5;IADD;ILOAD 4;IADD;ILOAD 3;LDC 10;ICREATE 6 1;ISTORE 5;READINPUT;ICREATE 5 1;ISTORE 4;READINPUT;ICREATE 4 1;ISTORE 3;READINPUT;ICREATE 3 1;IGLBLOAD 2 3 2;IGLBLOAD 1 3 1";

	
		SCExecuter sce = new SCExecuter( 
				Arrays.asList( str.split(";") ), 
				"1;2;3" 
			);
		sce.executeCoontract( "scName" );
	}	
	
}
