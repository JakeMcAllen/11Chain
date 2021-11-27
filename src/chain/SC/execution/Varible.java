package chain.SC.execution;

import javax.management.RuntimeErrorException;

public class Varible <T> {

	private int id=0;
	// TODO: Da far diventare una lista
	private T val = null;
	private TYPES type = null;
	
	// TODO: Controlli lista o item
	private int Item_List = 0;			// if "0" is item. "1" if it's a list
	
	public Varible(int id, T val, TYPES tp, int il) {
		super();
		this.id = id;
		this.val = val;
		
		this.type = tp;
		this.Item_List = il;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Object getVal() {
		return val;
	}
	public void setVal(T val) {
		this.val = val;
	}
	
	
	
	public enum TYPES {
		
		NUMBER	( Integer.class, 1 ),
		BOOLEAN	( Boolean.class, 2 ),
		STRING	( String.class, 3 );

		private final Class clss;
		private final int id;
		
		
		
		private TYPES(Class tp, int id) {
			this.clss = tp;
			this.id = id;
		}
		

		
		public Class getClss() {
			return clss;
		}
		
		public static TYPES getClass(String id) {
			for ( TYPES tp : TYPES.values() ) {
				if (tp.getId() == Integer.parseInt( id ) ) return tp;
			}
			
			return null;
		}

		public int getId() {
			return id;
		}
		
	}
	
	
	public void setType(TYPES type) {
		this.type = type;
	}
	
	public static TYPES getType ( Object iptObj ) {
		if (iptObj == null) {
			throw new RuntimeErrorException(null, "Empty object ");
		}
		try {
			double d = Double.parseDouble( iptObj.toString() );
			return TYPES.NUMBER;
		} catch (NumberFormatException nfe) {
			
			try {
			
				String str = iptObj.toString();
				
				if ( str.equals("true") || str.equals("false") ) {
					return TYPES.BOOLEAN;
				} else {
					return TYPES.STRING;
				}
					
					
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeErrorException(null, "Illegal error ");
			}
		}
	}
	
	
	public TYPES getType() {
		return type;
	}
	
}
