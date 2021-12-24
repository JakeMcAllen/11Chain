package chain.SC.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import chain.SC.execution.Varible.TYPES;

public class SCExecuter implements SCEinterface {


	// Global variables 
	/**
	 * 	Contain list of bytecode to analyze
	 */
	private List<String> listBytecode = null;

	/**
	 * 	Contail list of variable used in S.C.
	 * 	
	 * 	The first is for 
	 * 
	 */
	private Map<String, Map<String, Varible>> listVariable = null;
	private Map<String, Varible> listGlobalVariable = null;

	/**
	 * 	Space where are stored all variable and 
	 * 
	 * 	The first param contain the name of contract/function,
	 * 	The second on is a list of object.
	 * 
	 */
	private Map<String, List<Object>> heap = null;

	// I/O
	private String output = "";
	private String[] inputVar = null;
	private int inputVarCounter = 0;

	// Bytecode
	private String bytecode = null;
	private int bytecodeIndex = 0;

	

	public SCExecuter(List<String> inputStr, String inputVar) {

		this.listBytecode = inputStr;

		this.listVariable = new HashMap<String, Map<String, Varible>> ();
		this.listGlobalVariable = new HashMap<String, Varible> ();

		this.heap = new HashMap<String, List<Object>>();

		
		this.inputVar = inputVar.split(";");
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
	public void error(String error) {
		throw new RuntimeErrorException(null, error);
	}




	@SuppressWarnings("unchecked")
	@Override
	public void executeCoontract(String ctName) {

		// TODO carica il codice da eseguire 

		this.listVariable.put(ctName, new HashMap<String, Varible> ());
		heap.put(ctName, new ArrayList<Object> ());
		boolean rtn = false;
		

		while ( move() && !rtn ) {

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
				var = pop(ctName);

				try {
					TYPES tp = Varible.TYPES.getClass( cmd[2] );
					Varible<?> vr = null;

					if ( Varible.getType( var ).getClss() == String.class ) {
						vr = new Varible<String>( Integer.parseInt( cmd[1] ), (String) var, tp, Integer.parseInt( cmd[2] ) );
					} else if ( Varible.getType( var ).getClss() == Integer.class ) {
						vr = new Varible<Integer>( Integer.parseInt( cmd[1] ), (int) var, tp, Integer.parseInt( cmd[2] ) );
					} else if ( Varible.getType( var ).getClss() == Boolean.class ) {
						vr = new Varible<Boolean>( Integer.parseInt( cmd[1] ), (Boolean) var, tp, Integer.parseInt( cmd[2] ) );
					} else {
						throw new RuntimeErrorException(null, "Impossible check variable type for creation method");
					}

					listVariable.get(ctName).put(ctName, vr);
				} catch (Exception e) {
					throw new RuntimeErrorException(null, "Internal error in variable creation. Check the code");
				}
				break;

			case "IlOAD":
				var = listVariable.get( cmd[1] );
				add(ctName, var);
				break;

			case "IGLBLOAD":
				var = pop(ctName);
				
				try {
					TYPES tp = Varible.TYPES.getClass( cmd[2] );
					Varible<?> vr = null;

					if ( Varible.getType( var ).getClss() == String.class ) {
						vr = new Varible<String>( Integer.parseInt( cmd[1] ), (String) var, tp, Integer.parseInt( cmd[3] ) );
					} else if ( Varible.getType( var ).getClss() == Integer.class ) {
						vr = new Varible<Integer>( Integer.parseInt( cmd[1] ), (int) var, tp, Integer.parseInt( cmd[3] ) );
					} else if ( Varible.getType( var ).getClss() == Boolean.class ) {
						vr = new Varible<Boolean>( Integer.parseInt( cmd[1] ), (Boolean) var, tp, Integer.parseInt( cmd[3] ) );
					} else {
						throw new RuntimeErrorException(null, "Impossible check variable type for global creation method");
					}

					listGlobalVariable.put(cmd[1], vr );
				} catch (Exception e) {
					throw new RuntimeErrorException(null, "Internal error in global variable creation. Check the code");
				}
				
				
				break;

			case "IADD":
				var = pop(ctName);
				var1 = pop(ctName);

				if ( Varible.getType( var ).getClss() == Integer.class
						&& Varible.getType( var1 ).getClss() == Integer.class ) {
					add(ctName, Integer.parseInt( var.toString() ) + Integer.parseInt( var1.toString() ) );
				} else if ( Varible.getType( var ).getClss() == Boolean.class 
						|| Varible.getType( var1 ).getClss() == Boolean.class ) {
					throw new RuntimeErrorException(null, "Not possible add a bollean variable");
				} else if ( Varible.getType( var ).getClss() == String.class 
						|| Varible.getType( var1 ).getClss() == String.class ) {
					add(ctName, var.toString().concat( var1.toString() ));
				} else {
					throw new RuntimeErrorException(null, "Not possible add a not definited variable");
				}

				break;

			case "IMIN":
				var = pop(ctName);
				var1 = pop(ctName);

				if ( Varible.getType( var ).getClss() == Integer.class
						&& Varible.getType( var1 ).getClss() == Integer.class ) {
					add(ctName, Integer.parseInt( var.toString() ) + Integer.parseInt( var1.toString() ) );
				} else {
					throw new RuntimeErrorException(null, "Not possible subtract a bollean variable");
				}

				break;

			case "IMOLT":
				var = pop(ctName);
				var1 = pop(ctName);

				if ( Varible.getType( var ).getClss() == Integer.class
						&& Varible.getType( var1 ).getClss() == Integer.class ) {
					add(ctName, Integer.parseInt( var.toString() ) + Integer.parseInt( var1.toString() ) );
				} else if ( Varible.getType( var ).getClss() == Boolean.class 
						|| Varible.getType( var1 ).getClss() == Boolean.class ) {
					throw new RuntimeErrorException(null, "Not possible moliplicate a bollean variable");
				} else if ( Varible.getType( var ).getClss() == String.class 
						|| Varible.getType( var1 ).getClss() == Integer.class ) {

					String str = "";
					for (int i = 0; i < Integer.parseInt( var1.toString() ); i++) {
						str.concat( var.toString() );
					}

					add(ctName, str);
				}  else {
					throw new RuntimeErrorException(null, "Not possible moliplicate a not definited variable");
				}

				break;

			case "IDIV":
				var = pop(ctName);
				var1 = pop(ctName);

				if ( Varible.getType( var ).getClss() == Integer.class && Varible.getType( var1 ).getClss() == Integer.class ) {
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


				if ( Varible.getType( var ).getClss() == String.class && Varible.getType( var ).getClss() == String.class ) {
					bl = var.toString().equals( var1.toString() );

				} else if ( Varible.getType( var ).getClss() == Boolean.class && Varible.getType( var ).getClss() == Boolean.class ) {
					bl = Boolean.getBoolean( var.toString() ) == Boolean.getBoolean( var1.toString() );

				} else if ( Varible.getType( var ).getClss() == Integer.class && Varible.getType( var ).getClss() == Integer.class ) {
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


				if ( Varible.getType( var ).getClss() == String.class && Varible.getType( var ).getClss() == String.class ) {
					bl = !var.toString().equals( var1.toString() );

				} else if ( Varible.getType( var ).getClss() == Boolean.class && Varible.getType( var ).getClss() == Boolean.class ) {
					bl = Boolean.getBoolean( var.toString() ) != Boolean.getBoolean( var1.toString() );

				} else if ( Varible.getType( var ).getClss() == Integer.class && Varible.getType( var ).getClss() == Integer.class ) {
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


				if ( Varible.getType( var ).getClss() == Integer.class && Varible.getType( var ).getClss() == Integer.class ) {
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


				if ( Varible.getType( var ).getClss() == Integer.class && Varible.getType( var ).getClss() == Integer.class ) {
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


				if ( Varible.getType( var ).getClss() == Integer.class && Varible.getType( var ).getClss() == Integer.class ) {
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


				if ( Varible.getType( var ).getClss() == Integer.class && Varible.getType( var ).getClss() == Integer.class ) {
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


				if ( Varible.getType( var ).getClss() == Boolean.class && Varible.getType( var ).getClss() == Boolean.class ) {
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


				if ( Varible.getType( var ).getClss() == Boolean.class && Varible.getType( var ).getClss() == Boolean.class ) {
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
	
						this.output.concat( var.toString() );
						break;
	
					case "CONTR":
						var = pop(ctName);
						vl = Integer.parseInt( cmd[2].substring(1, cmd[2].length() ) );
	
						
						if ( Varible.getType( var ).getClss() == Boolean.class ) {
	
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

				throw new IllegalArgumentException("Unexpected value: " + cmd[0] );	

			}
		}
	}


	@Override
	public Object pop(String ct) {
		int position = heap.get(ct).size() -1;

		return heap.get(ct).remove( position );
	}


	@Override
	public void add(String ct, Object obj) {

		heap.get(ct).add(obj);
	}

	public String getOutput() {
		return this.output;
	}
	
}
