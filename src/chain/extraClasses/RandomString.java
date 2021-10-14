package chain.extraClasses;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomString {

    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String lower = upper.toLowerCase(Locale.ROOT);
    public static final String digits = "0123456789";

    public static final String alphanum = upper + lower + digits;


    
    // configurations
    private static int standardLength = 255;
	
	
    /**
     * Generate a random string.
     */
    public static String newString(int length) {
    	
    	try {
	    	Random random = ThreadLocalRandom.current();

	    	
			if (length < 1) throw new IllegalArgumentException();
	        if (alphanum.length() < 2) throw new IllegalArgumentException();
	        random = Objects.requireNonNull(random);
	        char[] symbols = alphanum.toCharArray();
	        char[] buf = new char[length];
	    	
	    	
	        for (int idx = 0; idx < buf.length; ++idx)
	            buf[idx] = symbols[random.nextInt(symbols.length)];
	        return new String(buf);
    	
    	} catch (Exception e) {
    		return "";
    	}
    }
    
    public static String newString() {
    	return newString( standardLength );
    }


}