package chain.SC.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import chain.SC.execution.Variable.TYPES;

public class SCExecuter implements SCEinterface {


	// Global variables 
	/**
	 * 	Contain list of bytecode to analyze
	 */
	private List<String> listBytecode = null;

	/**
	 * 	Contain the list of variable used in S.C.
	 * 	
	 * 	The first parameter mark the contract / function that insert the variable.
	 * 	The second parameter is a tuple:
	 * 		1. Name of variable;
	 * 		2. Variable content
	 * 
	 */
	private Map<String, Map<String, Variable>> listVariable = null;
	private Map<String, Variable> listGlobalVariable = null;

	/**
	 * 	Space where are stored all variable and 
	 * 
	 * 	The first parameter contain the name of contract/function,
	 * 	The second on is a list of all objects in the heap.
	 * 		It is ordered
	 * 
	 */
	private Map<String, List<Object>> heap = null;
	
	/**
	 * 	List of contract and function
	 * 
	 * 	Contain a list of all contract and their position in "listBytecode".
	 * 	This is useful for call to action, for control and for organization
	 * 
	 */
	private Map<String, Integer> contractPosizion = null;
	private Map<String, Integer> functionPosizion = null;

	
	
	// I/O
	private String output = "";
	private String[] inputVar = null;
	private int inputVarCounter = 0;

	// Bytecode
	private String bytecode = null;
	private int bytecodeIndex = 0;

	

	public SCExecuter(List<String> inputStr, String inputVar, String contract) {

		this.listBytecode = inputStr;

		this.listVariable = new HashMap<String, Map<String, Variable>> ();
		this.listGlobalVariable = new HashMap<String, Variable> ();

		this.heap = new HashMap<String, List<Object>>();

		// TODO change variable "inputVar" to arrays
		this.inputVar = inputVar.split(";");
		
		
		// TODO: Check position of all contract / function
		int idx = 0;
		contractPosizion = new HashMap<String, Integer> ();
		functionPosizion = new HashMap<String, Integer> ();
		for ( String comd : listBytecode ) {
			
			if (comd.contains("CONTRACT")) {
				contractPosizion.put(comd.split(" ")[1], idx );
			} else if (comd.contains("FUNCTION")) {
				functionPosizion.put(comd.split(" ")[1], idx );
			}
			
			idx++;
		};
		
		// TODO: Esecuzione delle mapping e importing
		executeMappImp();
		
		
		// execution of a specific contract
		if (contractPosizion.containsKey(contract)) {
			executeCoontract(contract);
		} else {
			// TODO: Return an error message.
		}
		
	}


	@Override
	public boolean move() {
		// TODO: To check

		try {
			this.bytecode = this.listBytecode.get(this.bytecodeIndex++);

		} catch (IndexOutOfBoundsException e) {
			error("End execution withut a return message ");
			return false;

		} catch (Exception e) {
			error("Internal error. Check the code and try to restart");
			return false;

		}

		return true;
	}
	
	@Override
	public void moveTo(int idx) {
		try {
			this.bytecode = this.listBytecode.get(idx);
			this.bytecodeIndex = idx + 1;
		} 
		catch (IndexOutOfBoundsException e) { error("End execution withut a return message "); } 
		catch (Exception e) { error("Internal error. Check the code and try to restart"); }
	}

	@Override
	public void error(String error) {
		throw new RuntimeErrorException(null, error);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void executeCoontract(String ctName) {

		// Load code to execute
		if (this.contractPosizion.containsKey(ctName)) {
			moveTo(this.contractPosizion.get(ctName));
		} else if (this.functionPosizion.containsKey(ctName)) {
			moveTo(this.functionPosizion.get(ctName));
		} else {
			this.error("The contract/function : " + ctName + " It is not in the exection istruction.");
		}
		
		this.listVariable.put(ctName, new HashMap<String, Variable> ());
		heap.put(ctName, new ArrayList<Object> ());
		boolean rtn = false;
		
		

		while ( !rtn && move() ) {

			String[] cmd = this.bytecode.split(" ");
			Object var = null;
			Object var1 = null;
			Boolean bl = null;
			int vl = 0;


			// Call for the first word of every bytecode comand. 
			switch ( cmd[0] ) {
			case "LDC":
				add(ctName, cmd[1]);
				break;

			case "ISTORE":
				var = pop(ctName);

				if ( listVariable.get(ctName).containsKey( cmd[1] ) ) {
					listVariable.get(ctName).get( cmd[1] ).setVal( var );

				} else if ( listGlobalVariable.containsKey( cmd[1] ) ) {
					listGlobalVariable.get( cmd[1] ).setVal( var );

				}
				break;

			case "ICREATE":

				try {
					TYPES tp = Variable.TYPES.getClass( cmd[2] );
					Variable<?> vr = null;
					vl = Integer.parseInt( cmd[1] );
					
					switch (tp.getId()) 
					{
					case 1:
						 vr = new Variable<Integer>(vl, 0, tp, 0);
						 break;
					case 2: 
						vr = new Variable<Boolean>(vl, false, tp, 0);
						break;
					
					case 3: 
						vr = new Variable<String>(vl, "", tp, 0);;
						break;
						
					default:
						error("Type not found");
					}

					listVariable.get(ctName).put(cmd[1], vr);
				} catch (Exception e) {
					throw new RuntimeErrorException(null, "Internal error in variable creation. Check the code");
				}
				break;

			case "ILOAD":
				var = listVariable.get(ctName).get( cmd[1] );
				add(ctName, ((Variable) var).getVal());
				break;

			case "IADD":
				var = pop(ctName);
				var1 = pop(ctName);

				if ( Variable.getType( var ).getClss() == Integer.class
						&& Variable.getType( var1 ).getClss() == Integer.class ) {
					add(ctName, Integer.parseInt( var.toString() ) + Integer.parseInt( var1.toString() ) );
				} else if ( Variable.getType( var ).getClss() == Boolean.class 
						|| Variable.getType( var1 ).getClss() == Boolean.class ) {
					throw new RuntimeErrorException(null, "Not possible ad)d a bollean variable");
				} else if ( Variable.getType( var ).getClss() == String.class 
						|| Variable.getType( var1 ).getClss() == String.class ) {
					add(ctName, var.toString() + var1.toString() );
				} else {
					throw new RuntimeErrorException(null, "Not possible add a not definited variable");
				}

				break;

			case "IMIN":
				var = pop(ctName);
				var1 = pop(ctName);

				if ( Variable.getType( var ).getClss() == Integer.class
						&& Variable.getType( var1 ).getClss() == Integer.class ) {
					add(ctName, Integer.parseInt( var.toString() ) - Integer.parseInt( var1.toString() ) );
				} else {
					throw new RuntimeErrorException(null, "Not possible subtract a bollean variable");
				}

				break;

			case "IMOLT":
				var = pop(ctName);
				var1 = pop(ctName);

				if ( Variable.getType( var ).getClss() == Integer.class
						&& Variable.getType( var1 ).getClss() == Integer.class ) {
					add(ctName, Integer.parseInt( var.toString() ) * Integer.parseInt( var1.toString() ) );
				} else if ( Variable.getType( var ).getClss() == Boolean.class 
						|| Variable.getType( var1 ).getClss() == Boolean.class ) {
					throw new RuntimeErrorException(null, "Not possible moliplicate a bollean variable");
				} else if ( Variable.getType( var ).getClss() == String.class 
						|| Variable.getType( var1 ).getClss() == Integer.class ) {

					String str = "";
					for (int i = 0; i < Integer.parseInt( var1.toString() ); i++) {
						str += var.toString();
					}

					add(ctName, str);
				}  else {
					throw new RuntimeErrorException(null, "Not possible moliplicate a not definited variable");
				}

				break;

			case "IDIV":
				var = pop(ctName);
				var1 = pop(ctName);

				if ( Variable.getType( var ).getClss() == Integer.class && Variable.getType( var1 ).getClss() == Integer.class ) {
					if ( Integer.parseInt( var1.toString() ) == 0 ) {
						throw new RuntimeErrorException(null, "Not possible division for 0 variable");
					}

					add(ctName, Integer.parseInt( var.toString() ) / Integer.parseInt( var1.toString() ) );
				} else {
					throw new RuntimeErrorException(null, "Not possible division a not numeric variable");
				}

				break;

			case "IF_ICMPEQ":
				var = pop(ctName);
				var1 = pop(ctName);

				bl = false;


				if ( Variable.getType( var ).getClss() == String.class && Variable.getType( var ).getClss() == String.class ) {
					bl = var.toString().equals( var1.toString() );

				} else if ( Variable.getType( var ).getClss() == Boolean.class && Variable.getType( var ).getClss() == Boolean.class ) {
					bl = Boolean.getBoolean( var.toString() ) == Boolean.getBoolean( var1.toString() );

				} else if ( Variable.getType( var ).getClss() == Integer.class && Variable.getType( var ).getClss() == Integer.class ) {
					bl = Integer.getInteger( var.toString() ) == Integer.getInteger( var1.toString() );

				} else {

					try {
						bl = var == var1;
					} catch (Exception e) {
						throw new RuntimeErrorException(null, "Error in IF_ICMPEQ operation");
					}

				}

				add(ctName, bl);
				break;

			case "IF_ICMPNE":
				var = pop(ctName);
				var1 = pop(ctName);

				bl = false;


				if ( Variable.getType( var ).getClss() == String.class && Variable.getType( var ).getClss() == String.class ) {
					bl = !var.toString().equals( var1.toString() );

				} else if ( Variable.getType( var ).getClss() == Boolean.class && Variable.getType( var ).getClss() == Boolean.class ) {
					bl = Boolean.getBoolean( var.toString() ) != Boolean.getBoolean( var1.toString() );

				} else if ( Variable.getType( var ).getClss() == Integer.class && Variable.getType( var ).getClss() == Integer.class ) {
					bl = Integer.getInteger( var.toString() ) != Integer.getInteger( var1.toString() );

				} else {

					try {
						bl = var != var1;
					} catch (Exception e) {
						throw new RuntimeErrorException(null, "Error in IF_ICMPNE operation");
					}

				}

				add(ctName, bl);
				break;

			case "IF_ICMPGE":
				var = pop(ctName);
				var1 = pop(ctName);

				bl = false;


				if ( Variable.getType( var ).getClss() == Integer.class && Variable.getType( var ).getClss() == Integer.class ) {
					bl = Integer.getInteger( var.toString() ) >= Integer.getInteger( var1.toString() );

				} else {
					throw new RuntimeErrorException(null, "Error in IF_ICMPGE operation");
				}

				add(ctName, bl);
				break;

			case "IF_ICMPLE":
				var = pop(ctName);
				var1 = pop(ctName);

				bl = false;


				if ( Variable.getType( var ).getClss() == Integer.class && Variable.getType( var ).getClss() == Integer.class ) {
					bl = Integer.getInteger( var.toString() ) <= Integer.getInteger( var1.toString() );

				} else {
					throw new RuntimeErrorException(null, "Error in IF_ICMPLE operation");
				}

				add(ctName, bl);
				break;

			case "IF_ICMPLT":
				var = pop(ctName);
				var1 = pop(ctName);

				bl = false;


				if ( Variable.getType( var ).getClss() == Integer.class && Variable.getType( var ).getClss() == Integer.class ) {
					bl = Integer.getInteger( var.toString() ) < Integer.getInteger( var1.toString() );

				} else {
					throw new RuntimeErrorException(null, "Error in IF_ICMPLT operation");
				}

				add(ctName, bl);
				break;

			case "IF_ICMPGT":
				var = pop(ctName);
				var1 = pop(ctName);

				bl = false;


				if ( Variable.getType( var ).getClss() == Integer.class && Variable.getType( var ).getClss() == Integer.class ) {
					bl = Integer.getInteger( var.toString() ) > Integer.getInteger( var1.toString() );

				} else {
					throw new RuntimeErrorException(null, "Error in IF_ICMPGT operation");
				}

				add(ctName, bl);
				break;

			case "IAND":
				var = pop(ctName);
				var1 = pop(ctName);

				bl = false;


				if ( Variable.getType( var ).getClss() == Boolean.class && Variable.getType( var ).getClss() == Boolean.class ) {
					bl = Boolean.getBoolean( var.toString() ) && Boolean.getBoolean( var1.toString() );

				} else {
					throw new RuntimeErrorException(null, "Error in IF_ICMPGT operation");
				}

				add(ctName, bl);
				break;

			case "IOR":
				var = pop(ctName);
				var1 = pop(ctName);

				bl = false;


				if ( Variable.getType( var ).getClss() == Boolean.class && Variable.getType( var ).getClss() == Boolean.class ) {
					bl = Boolean.getBoolean( var.toString() ) || Boolean.getBoolean( var1.toString() );

				} else {
					throw new RuntimeErrorException(null, "Error in IF_ICMPGT operation");
				}

				add(ctName, bl);
				break;

			case "READINPUT":
				var = this.inputVar[ this.inputVarCounter++ ];
				
				heap.get(ctName).add(var);
				break;

			case "GOTO":
				vl = Integer.parseInt( cmd[1].substring(1, cmd[1].length() ) );

				while ( true ) { 
					if ( this.bytecode.substring(0, 1) == "L" && this.bytecode.length() >= 2 ) {
						if ( Integer.parseInt( this.bytecode.split(" ")[1] ) == vl ) {
							break;
					} }
					
					move(); 
				}
				break;

			case "INVOKEVIRTUAL":

				switch (cmd[1]) {
					case "PRINT":
						var = pop(ctName);
	
						this.output += var.toString();
						break;
	
					case "CONTR":
						var = pop(ctName);
						vl = Integer.parseInt( cmd[2].substring(1, cmd[2].length() ) );
	
						
						if ( Variable.getType( var ).getClss() == Boolean.class ) {
	
							if ( Boolean.parseBoolean( (String) var ) ) {
	
								while ( true ) { 
									if ( this.bytecode.substring(0, 1) == "L" && this.bytecode.length() >= 2 ) {
										if ( Integer.parseInt( this.bytecode.split(" ")[1] ) == vl ) {
											break;
									} }
									
									move(); 
								}
								
							}
							
							
						} else throw new RuntimeErrorException(null, "Error on input contro for CONTR");
						
						break;
	
					case "RETURN":
						rtn = true;
						break;
	
					default:
						throw new IllegalArgumentException("Unexpected value: " + cmd[1] );
				}

				break;


			default:
				throw new IllegalArgumentException("Unexpected value: " + cmd );	

			}
		}

	}
	
	
	@Override
	public String getEsecutionOutput() {
		return this.output;
	}


	@Override
	public Object pop(String ct) {
		int position = heap.get(ct).size() - 1;
		
		return heap.get(ct).remove( position );
	}


	@Override
	public void add(String ct, Object obj) {
		heap.get(ct).add(obj);
	}

	public String getOutput() {
		return this.output;
	}



	
	private void executeMappImp() {
		
		for (String act : listBytecode) {
			
			String[] cmd = act.split(" ");
			int vl = 0;

			switch (cmd[0]) {
			case "IGLBLOAD":
				// TODO: item e list da distinguere e da trrattarre in modo diverso
				vl = Integer.parseInt( cmd[1] );
				
				try {
					TYPES tp = Variable.TYPES.getClass( cmd[2] );
					Variable<?> vr = null;
					
					switch (tp.getId()) 
					{
					case 1:
						 vr = new Variable<Integer>(vl, 0, tp, 0);
						 break;
					case 2: 
						vr = new Variable<Boolean>(vl, false, tp, 0);
						break;
					
					case 3: 
						vr = new Variable<String>(vl, "", tp, 0);;
						break;
						
					default:
						error("Type not found");
					}
					
					listGlobalVariable.put(cmd[1], vr );
				} catch (Exception e) {
					throw new RuntimeErrorException(null, "Internal error in global variable creation. Check the code");
				}
				
				
				// TODO: import and default
				
				break;
			
			}
			
		}
		
	}


	
}
