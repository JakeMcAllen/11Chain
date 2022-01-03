package chain.SC.execution;

import java.util.List;

public interface SCEinterface {

	
	
	/**
	 * 	Moves forward one position with reading of bytecode list 
	 * @return 
	 * 
	 */
	public boolean move();
	
	/**
	 * 	Move the execution to the the instructions with the specified tag
	 * 	In fact load to variable: "bytecode" the instruction at specified place 
	 * 
	 * @param idx the index of instruction to load at "bytecode" variable
	 */
	public void moveTo(int idx);
	
	/**
	 * 	Throw an runtime exception thath return a to user a message.
	 * 
	 * @param error String The message to return
	 */
	public void error(String error);
	
	/**
	 * 	Execute bytecode comands 
	 * 
	 * @param ctName name of "contract" to execute
	 */
	public void executeCoontract(String ctName);
	
	/**
	 * 	Return to the caller to output of execution of a SmartContract
	 * 	Output, String and return value are printed in a string thanks is return thanks to this method
	 * 
	 * @return String of execution
	 */
	public String getEsecutionOutput();
	
	/**
	 * 	Remove the value on top of heap and return it 
	 * 
	 * @param ct String the name of contract
	 */
	public Object pop(String ct);
	
	/**
	 * 	Add a value on top of the heap
	 * 
	 * @param ct String the name of contract
	 * @param obj The object to put on top of heap
	 */
	public void add(String ct, Object obj);
}
