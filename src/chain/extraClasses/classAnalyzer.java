package chain.extraClasses;

public class classAnalyzer {

	public static boolean isNumber(Object  var ) {
	    if ( var  == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble( var.toString() );
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

}
