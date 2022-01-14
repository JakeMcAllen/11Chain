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
 
//		String str = "CONTRACT p 1;LDC 10;INVOKEVIRTUAL PRINT;INVOKEVIRTUAL RETURN"; 					// funziona
//		String str = "CONTRACT p 1;LDC 2;LDC 11;IDIV;INVOKEVIRTUAL PRINT;INVOKEVIRTUAL RETURN"; 		// funziona

		
		String str = "IGLBLOAD 1 3 1;IGLBLOAD 2 3 2;CONTRACT p 1;ICREATE 3 1;READINPUT;ISTORE 3;ICREATE 4 1;READINPUT;ISTORE 4;ICREATE 5 1;READINPUT;ISTORE 5;ICREATE 6 1;LDC 10;ILOAD 3;IADD;ILOAD 4;IADD;ILOAD 5;IADD;ISTORE 6;ILOAD 6;LDC 10;IADD;INVOKEVIRTUAL PRINT;INVOKEVIRTUAL RETURN";
		
		
		// String str = "IGLBLOAD 1 3 1;IGLBLOAD 2 3 2;CONTRACT p 1;ICREATE 3 1;READINPUT;ISTORE 3;ICREATE 4 1;READINPUT;ISTORE 4;ICREATE 5 1;READINPUT;ISTORE 5;ICREATE 6 1;LDC 10;ILOAD 3;IADD;ILOAD 4;IADD;ILOAD 5;IADD;ISTORE 6;ILOAD 6;LDC 10;IADD;INVOKEVIRTUAL PRINT;ILOAD 6;LDC 0;IF_ICMPGT;INVOKEVIRTUAL CONTR L0;GOTO L1;L0;ILOAD 6;LDC 10;IADD;INVOKEVIRTUAL PRINT;GOTO L2;L1;ILOAD 6;LDC 0;IF_ICMPLT;INVOKEVIRTUAL CONTR L3;GOTO L4;L3;ILOAD 6;INVOKEVIRTUAL PRINT;GOTO L2;L4;LDC 0;INVOKEVIRTUAL PRINT;GOTO L2;L2;ILOAD 6;LDC 1;IADD;LDC Ciao;IADD;LDC 10;IADD;ISTORE 1;LDC 10;INVOKESTATIC RETURN;";
		
	
		SCExecuter sce = new SCExecuter( 
				Arrays.asList( str.split(";") ), 
				"1;2;3",
				"p"
			);


		System.out.println("Output: " + sce.getEsecutionOutput() );
		
	}	
	
}
